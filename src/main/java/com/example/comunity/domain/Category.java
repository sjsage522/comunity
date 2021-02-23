package com.example.comunity.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.LifecycleState;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Category {

    @Id @GeneratedValue
    private Long categoryId;

    private String name;

    @OneToMany(mappedBy = "category")
    private final List<Board> boards = new ArrayList<>();

    protected Category() {

    }

    public Category(String name) {
        this.name = name;
    }

    public void addBoard(Board board) {
        if (!board.getCategory().boards.contains(board))
            board.changeCategory(this);
    }

    public static Category createCategory(String name) {
        return new Category(name);
    }
}
