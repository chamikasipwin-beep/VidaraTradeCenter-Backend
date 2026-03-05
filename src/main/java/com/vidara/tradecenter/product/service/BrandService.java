package com.vidara.tradecenter.product.service;

import com.vidara.tradecenter.product.dto.request.BrandRequest;
import com.vidara.tradecenter.product.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
    BrandResponse create(BrandRequest request);
    List<BrandResponse> getAll();
    BrandResponse getById(Long id);
    BrandResponse update(Long id, BrandRequest request);
    void delete(Long id);
}
