DROP TABLE IF EXISTS upload_file CASCADE;
DROP TABLE IF EXISTS comment CASCADE;
DROP TABLE IF EXISTS board CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP SEQUENCE IF EXISTS board_sequence;
DROP SEQUENCE IF EXISTS category_sequence;
DROP SEQUENCE IF EXISTS comment_sequence;
DROP SEQUENCE IF EXISTS upload_file_sequence;
DROP SEQUENCE IF EXISTS user_sequence;

CREATE SEQUENCE board_sequence START 1 INCREMENT 50;
CREATE SEQUENCE category_sequence START 1 INCREMENT 50;
CREATE SEQUENCE comment_sequence START 1 INCREMENT 50;
CREATE SEQUENCE upload_file_sequence START 1 INCREMENT 50;
CREATE SEQUENCE user_sequence START 1 INCREMENT 50;

CREATE TABLE users
(
    id                 bigint      NOT NULL,        --사용자 PK
    user_id            varchar(50) NOT NULL,        --사용자 id
    name               varchar(50) NOT NULL,        --사용자먕
    nick_name          varchar(50) NOT NULL,        --사용자 별명
    password           varchar(90) NOT NULL,        --사용자 비밀번호
    email              varchar(50) NOT NULL UNIQUE, --사용자 이메일
    created_date       timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE (user_id),
    UNIQUE (nick_name),
    UNIQUE (email)
);

CREATE TABLE category
(
    id                 bigint       NOT NULL, --카테고리 PK
    name               varchar(255) NOT NULL, --카테고리명
    created_date       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE BOARD
(
    id                 bigint       NOT NULL, --게시글 PK
    title              varchar(255) NOT NULL, --게시글 제목
    content            text         NOT NULL, --게시글 내용
    user_id            bigint       NOT NULL, --사용자 PK
    category_id        bigint       NOT NULL, --카테고리 PK
    created_date       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_board_to_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_board_to_category FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE comment
(
    id                 bigint    NOT NULL, --답글 PK
    content            text      NOT NULL, --답글 내용
    user_id            bigint    NOT NULL, --사용자 PK
    board_id           bigint    NOT NULL, --게시글 PK
    parent_id          bigint,             --부모 답글 PK
    created_date       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_comment_to_users FOREIGN KEY (user_id) REFERENCES comment (id),
    CONSTRAINT fk_comment_to_board FOREIGN KEY (board_id) REFERENCES board (id),
    CONSTRAINT fk_comment_to_comment FOREIGN KEY (parent_id) REFERENCES comment (id)
);

CREATE TABLE upload_file
(
    id                 bigint       NOT NULL, --첨부파일 PK
    board_id           bigint       NOT NULL, --게시글 PK
    original_file_name varchar(255) NOT NULL, --원본 파일명
    stored_file_name   varchar(255) NOT NULL, --저장 파일명
    file_download_uri  varchar(255) NOT NULL, --저장 경로
    file_type          varchar(255) NOT NULL, --파일 형식
    file_size          bigint       NOT NULL, --파일 크기
    created_date       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_upload_file_to_board FOREIGN KEY (board_id) REFERENCES board (id)
);