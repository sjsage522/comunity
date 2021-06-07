package com.example.comunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
        this.categoryName = categoryName;
    }

    /**
     * 카테고리 생성 메서드
     * @param name 카테고리 이름
     */
    public static Category of(final String name) {
        return new Category(name);
    }

    /**
     * 변경을 위한 추가 메서드 (카테고리 정보 수정)
     */
    public void modifyCategory(final String modifiedName) {
        this.categoryName = modifiedName;
    }
}
