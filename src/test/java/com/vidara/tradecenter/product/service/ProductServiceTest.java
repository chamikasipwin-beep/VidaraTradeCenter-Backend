package com.vidara.tradecenter.product.service;

import com.vidara.tradecenter.common.dto.PagedResponse;
import com.vidara.tradecenter.common.exception.BadRequestException;
import com.vidara.tradecenter.product.dto.request.ProductRequest;
import com.vidara.tradecenter.product.dto.response.ProductDetailResponse;
import com.vidara.tradecenter.product.dto.response.ProductResponse;
import com.vidara.tradecenter.product.mapper.ProductMapper;
import com.vidara.tradecenter.product.model.Category;
import com.vidara.tradecenter.product.model.Product;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
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

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        // Inject real mapper for deterministic mapping assertions.
        productService = new ProductServiceImpl(
                productRepository,
                categoryRepository,
                brandRepository,
                tagRepository,
                productImageRepository,
                new ProductMapper()
        );
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Organic Honey");
        request.setDescription("Natural forest honey");
        request.setSku("HONEY-001");
        request.setBasePrice(new BigDecimal("12.99"));
        request.setSalePrice(new BigDecimal("10.99"));
        request.setCategoryId(1L);
        request.setStatus(ProductStatus.ACTIVE);
        request.setStock(15);
        request.setTags(List.of("organic", "natural"));
        request.setImageUrls(List.of("https://cdn.example.com/honey-1.jpg"));

        Category category = new Category();
        category.setId(1L);
        category.setName("Food");

        Product saved = new Product();
        saved.setId(100L);
        saved.setName("Organic Honey");
        saved.setSku("HONEY-001");
        saved.setBasePrice(new BigDecimal("12.99"));
        saved.setStatus(ProductStatus.ACTIVE);
        saved.setStock(15);
        saved.setCategory(category);

        when(productRepository.existsBySku("HONEY-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductDetailResponse response = productService.create(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("HONEY-001", response.getSku());
        verify(productRepository, atLeastOnce()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should return paged products")
    void getAllWithPagination() {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("A");
        p1.setSku("SKU-A");
        p1.setBasePrice(new BigDecimal("10.00"));

        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("B");
        p2.setSku("SKU-B");
        p2.setBasePrice(new BigDecimal("20.00"));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(p1, p2), pageable, 2);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        PagedResponse<ProductResponse> response = productService.getAll(0, 10, "createdAt", "desc");

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPage());
        assertEquals(2, response.getTotalElements());
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should filter products by category")
    void filterByCategory() {
        Product product = new Product();
        product.setId(11L);
        product.setName("Category Product");
        product.setSku("CAT-001");
        product.setBasePrice(new BigDecimal("30.00"));

        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.filterProducts(eq(5L), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        PagedResponse<ProductResponse> response = productService.filter(
                5L, null, null, null, null,
                0, 10, "createdAt", "desc"
        );

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(1, response.getTotalElements());
        verify(productRepository).filterProducts(eq(5L), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("Should reject duplicate SKU")
    void duplicateSkuRejection() {
        ProductRequest request = new ProductRequest();
        request.setName("Duplicate SKU Product");
        request.setSku("DUP-001");
        request.setBasePrice(new BigDecimal("9.99"));
        request.setCategoryId(1L);
        request.setStock(1);

        when(productRepository.existsBySku("DUP-001")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> productService.create(request));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(productRepository, never()).save(any(Product.class));
    }
}
