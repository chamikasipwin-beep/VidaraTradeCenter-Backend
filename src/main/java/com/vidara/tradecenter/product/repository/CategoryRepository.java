package com.vidara.tradecenter.product.repository;

import com.vidara.tradecenter.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentCategoryIsNull();

    Optional<Category> findBySlug(String slug);

    Boolean existsByName(String name);
}
