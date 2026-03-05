package com.vidara.tradecenter.admin.service.impl;

import com.vidara.tradecenter.admin.dto.DashboardStatsResponse;
import com.vidara.tradecenter.admin.service.DashboardService;
import com.vidara.tradecenter.product.dto.response.ProductResponse;
import com.vidara.tradecenter.product.mapper.ProductMapper;
import com.vidara.tradecenter.product.model.Product;
import com.vidara.tradecenter.product.repository.CategoryRepository;
import com.vidara.tradecenter.product.repository.ProductRepository;
import com.vidara.tradecenter.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    public DashboardServiceImpl(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.productMapper = productMapper;
    }

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long totalProducts = productRepository.count();
        long totalCategories = categoryRepository.count();
        long totalUsers = userRepository.count();

        // Get 5 most recent products
        List<Product> recentProducts = productRepository.findAll(
                PageRequest.of(0, 5, Sort.by("createdAt").descending())).getContent();

        List<ProductResponse> recentProductResponses = recentProducts.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        return new DashboardStatsResponse(totalProducts, totalCategories, totalUsers, recentProductResponses);
    }
}
