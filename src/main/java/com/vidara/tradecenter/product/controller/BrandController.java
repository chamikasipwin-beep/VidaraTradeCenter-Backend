package com.vidara.tradecenter.product.controller;

import com.vidara.tradecenter.product.dto.request.BrandRequest;
import com.vidara.tradecenter.product.dto.response.BrandResponse;
import com.vidara.tradecenter.product.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    // ADMIN only (enforced in SecurityConfig)
    @PostMapping
    public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
        return new ResponseEntity<>(brandService.create(request), HttpStatus.CREATED);
    }

    // Public endpoint
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAll() {
        return ResponseEntity.ok(brandService.getAll());
    }

    // Public endpoint
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getById(id));
    }

    // ADMIN only
    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody BrandRequest request) {
        return ResponseEntity.ok(brandService.update(id, request));
    }

    // ADMIN only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
