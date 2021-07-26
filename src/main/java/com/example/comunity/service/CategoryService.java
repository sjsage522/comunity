package com.example.comunity.service;

import com.example.comunity.domain.Category;
import com.example.comunity.dto.category.CategoryCreateRequest;
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

    //TODO 관리자전용으로 수정해야함 (현재 컨트롤러 미구현임)
    @Transactional
    public Category create(final CategoryCreateRequest categoryCreateRequest) {

        String categoryName = categoryCreateRequest.getCategoryName();

        categoryRepository.findByCategoryName(categoryName)
                .ifPresent(category -> {
                    throw new DuplicateCategoryNameException();
                });

        Category newCategory = Category.of(categoryName);
        return categoryRepository.save(newCategory);
    }
}
