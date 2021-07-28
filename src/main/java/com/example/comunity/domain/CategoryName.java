package com.example.comunity.domain;

import lombok.Getter;

@Getter
public enum CategoryName {

    game("game", "게임"),
    coding("coding", "코딩");

    private String en;
    private String kr;

    CategoryName(String en, String kr) {
        this.en = en;
        this.kr = kr;
    }
}
