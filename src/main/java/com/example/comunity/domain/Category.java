package com.example.comunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SequenceGenerator(
        name = "category_sequence_generator",
        sequenceName = "category_sequence"
)
public class Category {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "category_sequence_generator"
    )
    private Long categoryId;

    @Column(unique = true)
    private String categoryName;

    private Category(final String categoryName) {
        if (categoryName == null || categoryName.isBlank()) throw new IllegalArgumentException("유효하지 않은 카테고리명 입니다.");
        this.categoryName = categoryName;
    }

    /**
     * 카테고리 생성 메서드
     * @param name 카테고리 이름
     */
    public static Category of(final String name) {
        return new Category(name);
    }

    // 변경을 위한 추가 메서드 (카테고리 정보 수정)
    public void modifyCategory(final String modifiedName) {
        this.categoryName = modifiedName;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
