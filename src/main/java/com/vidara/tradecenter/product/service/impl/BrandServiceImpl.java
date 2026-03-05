package com.vidara.tradecenter.product.service.impl;

import com.vidara.tradecenter.common.exception.DuplicateResourceException;
import com.vidara.tradecenter.common.exception.ResourceNotFoundException;
import com.vidara.tradecenter.common.util.SlugUtils;
import com.vidara.tradecenter.product.dto.request.BrandRequest;
import com.vidara.tradecenter.product.dto.response.BrandResponse;
import com.vidara.tradecenter.product.model.Brand;
import com.vidara.tradecenter.product.repository.BrandRepository;
import com.vidara.tradecenter.product.service.BrandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public BrandResponse create(BrandRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Brand", "name", request.getName());
        }
        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setSlug(SlugUtils.toSlug(request.getName()));
        return toBrandResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAll() {
        return brandRepository.findAll().stream()
                .map(this::toBrandResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
        return toBrandResponse(brand);
    }

    @Override
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setSlug(SlugUtils.toSlug(request.getName()));
        return toBrandResponse(brandRepository.save(brand));
    }

    @Override
    public void delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
        brandRepository.delete(brand);
    }

    private BrandResponse toBrandResponse(Brand brand) {
        BrandResponse response = new BrandResponse();
        response.setId(brand.getId());
        response.setName(brand.getName());
        response.setDescription(brand.getDescription());
        response.setSlug(brand.getSlug());
        response.setLogoUrl(brand.getLogoUrl());
        return response;
    }
}
