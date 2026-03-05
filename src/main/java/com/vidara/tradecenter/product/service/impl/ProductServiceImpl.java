package com.vidara.tradecenter.product.service.impl;

import com.vidara.tradecenter.common.dto.PagedResponse;
import com.vidara.tradecenter.common.exception.BadRequestException;
import com.vidara.tradecenter.common.exception.ResourceNotFoundException;
import com.vidara.tradecenter.common.util.SlugUtils;
import com.vidara.tradecenter.product.dto.request.ProductRequest;
import com.vidara.tradecenter.product.dto.response.ProductDetailResponse;
import com.vidara.tradecenter.product.dto.response.ProductResponse;
import com.vidara.tradecenter.product.mapper.ProductMapper;
import com.vidara.tradecenter.product.model.*;
import com.vidara.tradecenter.product.model.enums.ProductStatus;
import com.vidara.tradecenter.product.repository.*;
import com.vidara.tradecenter.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TagRepository tagRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            TagRepository tagRepository,
            ProductImageRepository productImageRepository,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.tagRepository = tagRepository;
        this.productImageRepository = productImageRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDetailResponse create(ProductRequest request) {
        // Validate unique SKU
        if (productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("Product with SKU '" + request.getSku() + "' already exists");
        }

        Product product = new Product();
        productMapper.updateProductFromRequest(request, product);
        product.setSlug(SlugUtils.toSlug(request.getName()));

        // Link category (required)
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        product.setCategory(category);

        // Link brand (optional)
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));
            product.setBrand(brand);
        }

        Product saved = productRepository.save(product);

        // Persist tags (create new ones if they don't exist)
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            List<Tag> tags = resolveOrCreateTags(request.getTags());
            saved.setTags(tags);
        }

        // Persist specifications
        if (request.getSpecifications() != null && !request.getSpecifications().isEmpty()) {
            List<ProductSpecification> specs = buildSpecifications(request.getSpecifications(), saved);
            saved.setSpecifications(specs);
        }

        // Persist images
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ProductImage> images = buildImages(request.getImageUrls(), saved);
            saved.setImages(images);
        }

        saved = productRepository.save(saved);
        return productMapper.toProductDetailResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAll(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductResponse> content = productPage.getContent().stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toProductDetailResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        return productMapper.toProductDetailResponse(product);
    }

    @Override
    public ProductDetailResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Validate SKU uniqueness (allow same product to keep its SKU)
        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("Product with SKU '" + request.getSku() + "' already exists");
        }

        productMapper.updateProductFromRequest(request, product);
        product.setSlug(SlugUtils.toSlug(request.getName()));

        // Update category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        product.setCategory(category);

        // Update brand
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));
            product.setBrand(brand);
        } else {
            product.setBrand(null);
        }

        // Replace tags
        if (request.getTags() != null) {
            product.getTags().clear();
            if (!request.getTags().isEmpty()) {
                product.setTags(resolveOrCreateTags(request.getTags()));
            }
        }

        // Replace specifications
        if (request.getSpecifications() != null) {
            product.getSpecifications().clear();
            if (!request.getSpecifications().isEmpty()) {
                product.setSpecifications(buildSpecifications(request.getSpecifications(), product));
            }
        }

        // Replace images (only if new URLs provided)
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            product.getImages().clear();
            product.setImages(buildImages(request.getImageUrls(), product));
        }

        return productMapper.toProductDetailResponse(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> filter(Long categoryId, Long brandId,
            BigDecimal minPrice, BigDecimal maxPrice,
            String search,
            int page, int size, String sortBy, String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        String searchTerm = (search != null && !search.isBlank()) ? "%" + search.trim() + "%" : null;
        Page<Product> productPage = productRepository.filterProducts(
                categoryId, brandId, minPrice, maxPrice, null, searchTerm, pageable);
        List<ProductResponse> content = productPage.getContent().stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, productPage);
    }

    // ── Private helpers ───────────────────────────────────────────────

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = "asc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private List<Tag> resolveOrCreateTags(List<String> tagNames) {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            String trimmed = tagName.trim();
            if (trimmed.isEmpty())
                continue;
            Tag tag = tagRepository.findByName(trimmed)
                    .orElseGet(() -> {
                        Tag newTag = new Tag(trimmed, SlugUtils.toSlug(trimmed));
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }
        return tags;
    }

    private List<ProductSpecification> buildSpecifications(
            List<ProductRequest.SpecificationEntry> entries, Product product) {
        List<ProductSpecification> specs = new ArrayList<>();
        for (ProductRequest.SpecificationEntry entry : entries) {
            if (entry.getKey() == null || entry.getKey().isBlank())
                continue;
            ProductSpecification spec = new ProductSpecification();
            spec.setSpecKey(entry.getKey().trim());
            spec.setSpecValue(entry.getValue() != null ? entry.getValue().trim() : "");
            spec.setProduct(product);
            specs.add(spec);
        }
        return specs;
    }

    private List<ProductImage> buildImages(List<String> imageUrls, Product product) {
        List<ProductImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            String url = imageUrls.get(i);
            if (url == null || url.isBlank())
                continue;
            ProductImage image = new ProductImage();
            image.setImageUrl(url.trim());
            image.setSortOrder(i);
            image.setIsPrimary(i == 0);
            image.setProduct(product);
            images.add(image);
        }
        return images;
    }
}
