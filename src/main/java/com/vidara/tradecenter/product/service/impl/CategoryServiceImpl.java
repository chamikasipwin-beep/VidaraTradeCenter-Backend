package com.vidara.tradecenter.product.service.impl;

import com.vidara.tradecenter.common.util.SlugUtils;
import com.vidara.tradecenter.product.dto.request.CategoryRequest;
import com.vidara.tradecenter.product.dto.response.CategoryResponse;
import com.vidara.tradecenter.product.mapper.CategoryMapper;
import com.vidara.tradecenter.product.model.Category;
import com.vidara.tradecenter.product.repository.CategoryRepository;
import com.vidara.tradecenter.product.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        Category category = categoryMapper.toCategory(request);
        category.setSlug(SlugUtils.toSlug(request.getName()));

        if (request.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(
                            () -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        List<Category> categories = categoryRepository.findByParentCategoryIsNull();
        return categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSlug(SlugUtils.toSlug(request.getName()));

        if (request.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(
                            () -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(updatedCategory);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }
}
