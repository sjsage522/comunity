package com.example.comunity.service;

import com.example.comunity.domain.Category;
import com.example.comunity.dto.category.CategoryCreateDto;
import com.example.comunity.exception.DuplicateCategoryNameException;
import com.example.comunity.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    @Transactional
    public Long create(final CategoryCreateDto categoryCreateDto) {

        String name = categoryCreateDto.getName();

        Category findCategoryByName = categoryRepository.findByName(name);
        if (findCategoryByName != null) {
            throw new DuplicateCategoryNameException("이미 존재하는 카테고리명 입니다.");
        }

        Category newCategory = Category.createCategory(name);
        return categoryRepository.create(newCategory);
    }
}
