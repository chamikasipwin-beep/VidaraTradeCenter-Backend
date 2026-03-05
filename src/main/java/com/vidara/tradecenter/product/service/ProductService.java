package com.vidara.tradecenter.product.service;

import com.vidara.tradecenter.common.dto.PagedResponse;
import com.vidara.tradecenter.product.dto.request.ProductRequest;
import com.vidara.tradecenter.product.dto.response.ProductDetailResponse;
import com.vidara.tradecenter.product.dto.response.ProductResponse;

import java.math.BigDecimal;

public interface ProductService {

    ProductDetailResponse create(ProductRequest request);

    PagedResponse<ProductResponse> getAll(int page, int size, String sortBy, String sortDir);

    ProductDetailResponse getById(Long id);

    ProductDetailResponse getBySlug(String slug);

    ProductDetailResponse update(Long id, ProductRequest request);

    void delete(Long id);

    PagedResponse<ProductResponse> filter(Long categoryId, Long brandId,
                                          BigDecimal minPrice, BigDecimal maxPrice,
                                          String search,
                                          int page, int size, String sortBy, String sortDir);
}
