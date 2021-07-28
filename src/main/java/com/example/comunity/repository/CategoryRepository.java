package com.example.comunity.repository;

import com.example.comunity.domain.Category;
import com.example.comunity.domain.CategoryName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryName(CategoryName categoryName);
}
