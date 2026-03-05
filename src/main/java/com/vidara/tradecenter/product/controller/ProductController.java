package com.vidara.tradecenter.product.controller;

import com.vidara.tradecenter.common.constants.AppConstants;
import com.vidara.tradecenter.common.dto.PagedResponse;
import com.vidara.tradecenter.product.dto.request.ProductRequest;
import com.vidara.tradecenter.product.dto.response.ProductDetailResponse;
import com.vidara.tradecenter.product.dto.response.ProductResponse;
import com.vidara.tradecenter.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ADMIN only (enforced in SecurityConfig)
    @PostMapping
    public ResponseEntity<ProductDetailResponse> create(@Valid @RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.create(request), HttpStatus.CREATED);
    }

    // Public — supports filtering via query params
    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        boolean hasFilter = categoryId != null || brandId != null
                || minPrice != null || maxPrice != null
                || (search != null && !search.isBlank());

        PagedResponse<ProductResponse> response = hasFilter
                ? productService.filter(categoryId, brandId, minPrice, maxPrice,
                        search, page, size, sortBy, sortDir)
                : productService.getAll(page, size, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    // Public
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    // Public
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductDetailResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }

    // ADMIN only
    @PutMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> update(@PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    // ADMIN only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
