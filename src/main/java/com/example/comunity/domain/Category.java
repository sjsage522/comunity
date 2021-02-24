package com.example.comunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Category {

    @Id @GeneratedValue
    private Long categoryId;

    private String name;

    @OneToMany(mappedBy = "category")
    private final List<Board> boards = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }

    /**
     * 게시판과 카테고리간의 연관관계 편의 메서드
     * @param board 특정 카테고리에 포함시킬 게시판
     */
    public void addBoard(Board board) {
        if (!board.getCategory().boards.contains(board)) board.changeCategory(this);
    }

    /**
     * 카테고리 생성 메서드
     * @param name 카테고리 이름
     */
    public static Category createCategory(String name) {
        return new Category(name);
    }
}
