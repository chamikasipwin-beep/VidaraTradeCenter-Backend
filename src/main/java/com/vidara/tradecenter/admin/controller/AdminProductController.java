package com.vidara.tradecenter.admin.controller;

import com.vidara.tradecenter.common.dto.PagedResponse;
import com.vidara.tradecenter.product.dto.request.ProductRequest;
import com.vidara.tradecenter.product.dto.response.ProductDetailResponse;
import com.vidara.tradecenter.product.dto.response.ProductResponse;
import com.vidara.tradecenter.product.model.enums.ProductStatus;
import com.vidara.tradecenter.product.service.BrandService;
import com.vidara.tradecenter.product.service.CategoryService;
import com.vidara.tradecenter.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    public AdminProductController(ProductService productService,
            CategoryService categoryService,
            BrandService brandService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        PagedResponse<ProductResponse> products = productService.filter(
                categoryId, brandId, minPrice, maxPrice, search,
                page, size, sortBy, sortDir);

        model.addAttribute("products", products.getContent());
        model.addAttribute("currentPage", products.getPage());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalElements", products.getTotalElements());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("brands", brandService.getAll());
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "admin/products/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("brands", brandService.getAll());
        return "admin/products/create";
    }

    @PostMapping("/create")
    public String createProduct(
            @ModelAttribute ProductRequest request,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String imageUrl,
            RedirectAttributes redirectAttributes) {
        try {
            request.setTags(parseCommaSeparated(tags));
            request.setImageUrls(parseCommaSeparated(imageUrl));
            if (request.getStatus() == null) {
                request.setStatus(ProductStatus.DRAFT);
            }
            productService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully");
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/products/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ProductDetailResponse product = productService.getById(id);
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAll());
            model.addAttribute("brands", brandService.getAll());
            return "admin/products/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/{id}/edit")
    public String editProduct(@PathVariable Long id,
            @ModelAttribute ProductRequest request,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String imageUrl,
            RedirectAttributes redirectAttributes) {
        try {
            request.setTags(parseCommaSeparated(tags));
            request.setImageUrls(parseCommaSeparated(imageUrl));
            if (request.getStatus() == null) {
                request.setStatus(ProductStatus.DRAFT);
            }
            productService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully");
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/products/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/products";
    }

    private List<String> parseCommaSeparated(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
