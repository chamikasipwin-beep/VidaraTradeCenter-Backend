package com.vidara.tradecenter.product.service;

import com.vidara.tradecenter.common.dto.PagedResponse;
import com.vidara.tradecenter.common.exception.BadRequestException;
import com.vidara.tradecenter.product.dto.request.ProductRequest;
import com.vidara.tradecenter.product.dto.response.ProductDetailResponse;
import com.vidara.tradecenter.product.dto.response.ProductResponse;
import com.vidara.tradecenter.product.mapper.ProductMapper;
import com.vidara.tradecenter.product.model.*;
import com.vidara.tradecenter.product.model.enums.ProductStatus;
import com.vidara.tradecenter.product.repository.*;
import com.vidara.tradecenter.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest productRequest;
    private Product product;
    private Category category;
    private Brand brand;
    private ProductDetailResponse detailResponse;
    private ProductResponse summaryResponse;

    @BeforeEach
    void setUp() {
        // ── Build a reusable ProductRequest ──────────────────────────
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("A test product description");
        productRequest.setSku("TEST-SKU-001");
        productRequest.setBasePrice(new BigDecimal("99.99"));
        productRequest.setSalePrice(new BigDecimal("79.99"));
        productRequest.setCategoryId(1L);
        productRequest.setBrandId(1L);
        productRequest.setStatus(ProductStatus.ACTIVE);
        productRequest.setStock(50);
        productRequest.setTags(List.of("electronics", "sale"));
        productRequest.setImageUrls(List.of("https://example.com/img1.jpg"));
        productRequest.setSpecifications(List.of(
                new ProductRequest.SpecificationEntry("Color", "Red")));

        // ── Build a reusable Category ────────────────────────────────
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setSlug("electronics");

        // ── Build a reusable Brand ───────────────────────────────────
        brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");
        brand.setSlug("test-brand");

        // ── Build a reusable Product entity ──────────────────────────
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSlug("test-product");
        product.setSku("TEST-SKU-001");
        product.setBasePrice(new BigDecimal("99.99"));
        product.setSalePrice(new BigDecimal("79.99"));
        product.setStatus(ProductStatus.ACTIVE);
        product.setStock(50);
        product.setCategory(category);
        product.setBrand(brand);
        product.setImages(new ArrayList<>());
        product.setSpecifications(new ArrayList<>());
        product.setTags(new ArrayList<>());

        // ── Build a reusable ProductDetailResponse ───────────────────
        detailResponse = new ProductDetailResponse();
        detailResponse.setId(1L);
        detailResponse.setName("Test Product");
        detailResponse.setSlug("test-product");
        detailResponse.setSku("TEST-SKU-001");
        detailResponse.setBasePrice(new BigDecimal("99.99"));
        detailResponse.setStatus(ProductStatus.ACTIVE);
        detailResponse.setImages(new ArrayList<>());
        detailResponse.setSpecifications(new ArrayList<>());
        detailResponse.setTags(new ArrayList<>());

        // ── Build a reusable ProductResponse (summary) ───────────────
        summaryResponse = new ProductResponse();
        summaryResponse.setId(1L);
        summaryResponse.setName("Test Product");
        summaryResponse.setSlug("test-product");
        summaryResponse.setSku("TEST-SKU-001");
        summaryResponse.setBasePrice(new BigDecimal("99.99"));
        summaryResponse.setStatus(ProductStatus.ACTIVE);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 1. Test createProduct()
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("create() should save product and return ProductDetailResponse")
    void createProduct_Success() {
        // Arrange
        when(productRepository.existsBySku("TEST-SKU-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(tagRepository.findByName("electronics")).thenReturn(Optional.empty());
        when(tagRepository.findByName("sale")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));
        when(productMapper.toProductDetailResponse(any(Product.class))).thenReturn(detailResponse);

        // Act
        ProductDetailResponse result = productService.create(productRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals("TEST-SKU-001", result.getSku());
        assertEquals(new BigDecimal("99.99"), result.getBasePrice());

        verify(productRepository).existsBySku("TEST-SKU-001");
        verify(categoryRepository).findById(1L);
        verify(brandRepository).findById(1L);
        verify(productRepository, times(2)).save(any(Product.class));
        verify(productMapper).updateProductFromRequest(eq(productRequest), any(Product.class));
        verify(productMapper).toProductDetailResponse(any(Product.class));
    }

    @Test
    @DisplayName("create() should save product without brand when brandId is null")
    void createProduct_WithoutBrand_Success() {
        // Arrange
        productRequest.setBrandId(null);
        productRequest.setTags(new ArrayList<>());
        productRequest.setImageUrls(new ArrayList<>());
        productRequest.setSpecifications(new ArrayList<>());

        when(productRepository.existsBySku("TEST-SKU-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toProductDetailResponse(any(Product.class))).thenReturn(detailResponse);

        // Act
        ProductDetailResponse result = productService.create(productRequest);

        // Assert
        assertNotNull(result);
        verify(brandRepository, never()).findById(anyLong());
    }

    // ═══════════════════════════════════════════════════════════════════
    // 2. Test getAll() with pagination
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getAll() should return paged response with correct pagination")
    void getAll_WithPagination_Success() {
        // Arrange
        List<Product> products = List.of(product);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toProductResponse(any(Product.class))).thenReturn(summaryResponse);

        // Act
        PagedResponse<ProductResponse> result = productService.getAll(0, 10, "createdAt", "desc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());
        assertEquals("Test Product", result.getContent().get(0).getName());

        verify(productRepository).findAll(any(Pageable.class));
        verify(productMapper).toProductResponse(product);
    }

    @Test
    @DisplayName("getAll() should return empty page when no products exist")
    void getAll_EmptyResult_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        PagedResponse<ProductResponse> result = productService.getAll(0, 10, "createdAt", "desc");

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("getAll() should sort ascending when sortDir is 'asc'")
    void getAll_AscendingSort_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toProductResponse(any(Product.class))).thenReturn(summaryResponse);

        // Act
        PagedResponse<ProductResponse> result = productService.getAll(0, 5, "name", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getSize());
        assertEquals(1, result.getContent().size());
    }

    // ═══════════════════════════════════════════════════════════════════
    // 3. Test filter by category
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("filter() should return products filtered by categoryId")
    void filter_ByCategoryId_Success() {
        // Arrange
        List<Product> products = List.of(product);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);

        when(productRepository.filterProducts(
                eq(1L), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(productPage);
        when(productMapper.toProductResponse(any(Product.class))).thenReturn(summaryResponse);

        // Act
        PagedResponse<ProductResponse> result = productService.filter(
                1L, null, null, null, null, 0, 10, "createdAt", "desc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());

        verify(productRepository).filterProducts(
                eq(1L), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("filter() should handle combined filters (category + brand + price + search)")
    void filter_CombinedFilters_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.filterProducts(
                eq(1L), eq(1L), eq(new BigDecimal("50")), eq(new BigDecimal("200")),
                isNull(), eq("laptop"), any(Pageable.class)))
                .thenReturn(productPage);
        when(productMapper.toProductResponse(any(Product.class))).thenReturn(summaryResponse);

        // Act
        PagedResponse<ProductResponse> result = productService.filter(
                1L, 1L, new BigDecimal("50"), new BigDecimal("200"),
                "laptop", 0, 10, "createdAt", "desc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("filter() should treat blank search string as null")
    void filter_BlankSearch_TreatedAsNull() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Product> productPage = new PageImpl<>(List.of(), pageable, 0);

        when(productRepository.filterProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(productPage);

        // Act
        PagedResponse<ProductResponse> result = productService.filter(
                null, null, null, null, "   ", 0, 10, "createdAt", "desc");

        // Assert
        assertNotNull(result);

        // Verify that blank search is passed as null
        verify(productRepository).filterProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }

    // ═══════════════════════════════════════════════════════════════════
    // 4. Test duplicate SKU rejection
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("create() should throw BadRequestException for duplicate SKU")
    void createProduct_DuplicateSku_ThrowsException() {
        // Arrange
        when(productRepository.existsBySku("TEST-SKU-001")).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> productService.create(productRequest)
        );

        assertTrue(exception.getMessage().contains("TEST-SKU-001"));
        assertTrue(exception.getMessage().contains("already exists"));

        // Verify that save was NEVER called
        verify(productRepository, never()).save(any(Product.class));
        verify(categoryRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("update() should throw BadRequestException when changing to an existing SKU")
    void updateProduct_DuplicateSku_ThrowsException() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setSku("ORIGINAL-SKU");

        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setSku("DUPLICATE-SKU");
        updateRequest.setCategoryId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySku("DUPLICATE-SKU")).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> productService.update(1L, updateRequest));

        assertTrue(exception.getMessage().contains("DUPLICATE-SKU"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("update() should allow keeping the same SKU without throwing exception")
    void updateProduct_SameSku_Succeeds() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setSku("TEST-SKU-001");
        existingProduct.setTags(new ArrayList<>());
        existingProduct.setSpecifications(new ArrayList<>());
        existingProduct.setImages(new ArrayList<>());

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
        when(productMapper.toProductDetailResponse(any(Product.class))).thenReturn(detailResponse);

        // Act — should NOT throw even though the SKU already exists (it's the same
        // product)
        ProductDetailResponse result = productService.update(1L, productRequest);

        // Assert
        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
        // existsBySku should NOT be called because the SKU hasn't changed
        verify(productRepository, never()).existsBySku(anyString());
    }
}
