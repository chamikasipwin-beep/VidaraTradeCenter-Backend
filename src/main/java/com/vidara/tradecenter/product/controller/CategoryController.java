package com.vidara.tradecenter.product.controller;

import com.vidara.tradecenter.product.dto.request.CategoryRequest;
import com.vidara.tradecenter.product.dto.response.CategoryResponse;
import com.vidara.tradecenter.product.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ADMIN only - requires authentication
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Public endpoint
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        List<CategoryResponse> responses = categoryService.getAll();
        return ResponseEntity.ok(responses);
    }

    // Public endpoint
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getById(id);
        return ResponseEntity.ok(response);
    }

    // ADMIN only - requires authentication
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // ADMIN only - requires authentication
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
