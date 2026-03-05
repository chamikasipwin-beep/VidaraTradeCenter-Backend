package com.vidara.tradecenter.product.repository;

import com.vidara.tradecenter.product.model.Product;
import com.vidara.tradecenter.product.model.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

       Page<Product> findByStatus(ProductStatus status, Pageable pageable);

       Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

       Page<Product> findByBrandId(Long brandId, Pageable pageable);

       Optional<Product> findBySlug(String slug);

       Optional<Product> findBySku(String sku);

       Boolean existsBySku(String sku);

       @Query("SELECT p FROM Product p WHERE " +
                     "LOWER(p.name) LIKE LOWER(:search) OR " +
                     "LOWER(p.description) LIKE LOWER(:search)")
       Page<Product> searchByNameOrDescription(@Param("search") String search, Pageable pageable);

       @Query("SELECT p FROM Product p WHERE " +
                     "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
                     "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
                     "(:minPrice IS NULL OR p.basePrice >= :minPrice) AND " +
                     "(:maxPrice IS NULL OR p.basePrice <= :maxPrice) AND " +
                     "(:status IS NULL OR p.status = :status) AND " +
                     "(:search IS NULL OR LOWER(p.name) LIKE LOWER(:search) OR " +
                     "LOWER(p.description) LIKE LOWER(:search))")
       Page<Product> filterProducts(
                     @Param("categoryId") Long categoryId,
                     @Param("brandId") Long brandId,
                     @Param("minPrice") BigDecimal minPrice,
                     @Param("maxPrice") BigDecimal maxPrice,
                     @Param("status") ProductStatus status,
                     @Param("search") String search,
                     Pageable pageable);
}
