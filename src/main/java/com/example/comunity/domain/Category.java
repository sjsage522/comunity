package com.example.comunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.example.comunity.domain.CategoryName.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "category")
@SequenceGenerator(
        name = "category_sequence_generator",
        sequenceName = "category_sequence"
)
public class Category {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "category_sequence_generator"
    )
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryName categoryName;

    private Category(final String categoryName) {
        if (categoryName == null || categoryName.isBlank()) throw new IllegalArgumentException("유효하지 않은 카테고리명 입니다.");
        this.categoryName = upperValueOf(categoryName);
    }

    /**
     * 카테고리 생성 메서드
     * @param name 카테고리 이름
     */
    public static Category from(final String name) {
        return new Category(name);
    }

    // 변경을 위한 추가 메서드 (카테고리 정보 수정)
    public void modifyCategory(final String modifiedName) {
        this.categoryName = upperValueOf(modifiedName);
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + id +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
