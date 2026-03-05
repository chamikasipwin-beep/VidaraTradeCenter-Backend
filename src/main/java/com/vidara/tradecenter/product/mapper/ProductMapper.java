package com.vidara.tradecenter.product.mapper;

import com.vidara.tradecenter.product.dto.request.ProductRequest;
import com.vidara.tradecenter.product.dto.response.BrandResponse;
import com.vidara.tradecenter.product.dto.response.CategoryResponse;
import com.vidara.tradecenter.product.dto.response.ProductDetailResponse;
import com.vidara.tradecenter.product.dto.response.ProductResponse;
import com.vidara.tradecenter.product.model.*;
import com.vidara.tradecenter.product.model.enums.ProductStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toProduct(ProductRequest request) {
        Product product = new Product();
        updateProductFromRequest(request, product);
        return product;
    }

    public ProductResponse toProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setSku(product.getSku());
        response.setBasePrice(product.getBasePrice());
        response.setSalePrice(product.getSalePrice());
        response.setStatus(product.getStatus());
        response.setStock(product.getStock());
        response.setPrimaryImageUrl(resolvePrimaryImageUrl(product));
        response.setCategory(toCategoryRef(product.getCategory()));
        response.setBrand(toBrandRef(product.getBrand()));
        return response;
    }

    public ProductDetailResponse toProductDetailResponse(Product product) {
        ProductDetailResponse response = new ProductDetailResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setSku(product.getSku());
        response.setBasePrice(product.getBasePrice());
        response.setSalePrice(product.getSalePrice());
        response.setStatus(product.getStatus());
        response.setStock(product.getStock());
        response.setDescription(product.getDescription());
        response.setWeight(product.getWeight());
        response.setDimensions(product.getDimensions());
        response.setPrimaryImageUrl(resolvePrimaryImageUrl(product));
        response.setCategory(toCategoryRef(product.getCategory()));
        response.setBrand(toBrandRef(product.getBrand()));

        // Images
        if (product.getImages() != null) {
            response.setImages(product.getImages().stream()
                    .map(img -> new ProductDetailResponse.ImageResponse(
                            img.getId(), img.getImageUrl(), img.getAltText(),
                            img.getSortOrder(), img.getIsPrimary()))
                    .collect(Collectors.toList()));
        } else {
            response.setImages(new ArrayList<>());
        }

        // Specifications
        if (product.getSpecifications() != null) {
            response.setSpecifications(product.getSpecifications().stream()
                    .map(s -> new ProductDetailResponse.SpecificationResponse(
                            s.getId(), s.getSpecKey(), s.getSpecValue()))
                    .collect(Collectors.toList()));
        } else {
            response.setSpecifications(new ArrayList<>());
        }

        // Tags (as plain name strings)
        if (product.getTags() != null) {
            response.setTags(product.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));
        } else {
            response.setTags(new ArrayList<>());
        }

        return response;
    }

    public void updateProductFromRequest(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setBasePrice(request.getBasePrice());
        product.setSalePrice(request.getSalePrice());
        product.setStatus(request.getStatus() != null ? request.getStatus() : ProductStatus.DRAFT);
        product.setWeight(request.getWeight());
        product.setDimensions(request.getDimensions());
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
    }

    // ── Private helpers ───────────────────────────────────────────────

    private String resolvePrimaryImageUrl(Product product) {
        List<ProductImage> images = product.getImages();
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(images.get(0).getImageUrl());
    }

    private CategoryResponse toCategoryRef(Category category) {
        if (category == null) return null;
        CategoryResponse ref = new CategoryResponse();
        ref.setId(category.getId());
        ref.setName(category.getName());
        ref.setSlug(category.getSlug());
        return ref;
    }

    private BrandResponse toBrandRef(Brand brand) {
        if (brand == null) return null;
        BrandResponse ref = new BrandResponse();
        ref.setId(brand.getId());
        ref.setName(brand.getName());
        ref.setSlug(brand.getSlug());
        return ref;
    }
}
