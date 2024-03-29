package com.example.comunity.domain;

import lombok.Getter;

@Getter
public enum CategoryName {

    GAME("GAME", "게임"),
    CODING("CODING", "코딩");

    private String en;
    private String kr;

    CategoryName(final String en, final String kr) {
        this.en = en;
        this.kr = kr;
    }

    public static CategoryName upperValueOf(final String name) {
        return CategoryName.valueOf(name.toUpperCase());
    }
}
