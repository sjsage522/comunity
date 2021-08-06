# Comunity

- <span style="color:grey">Comunity</span> : My First Project for Spring Boot 
  - 개발도구 : InteliJ IDE
  - 언어 : JAVA
  - SpringBoot와 JPA를 활용한 게시판 API 구현
***
- [1. 요구사항]
- [2. 개념적 설계와 논리적 설계]
- [3. 테이블 명세]
- [4. API]
  - [4.1 사용자 API]
    - [4.1.1 사용자 회원가입]
    - [4.1.2 사용자 로그인]
    - [4.1.3 로그아웃]
    - [4.1.4 모든 사용자 조회]
    - [4.1.5 사용자 단건 조회]
    - [4.1.6 사용자 정보 수정]
    - [4.1.7 사용자 정보 삭제]
  - [4.2 게시글 API]
    - [4.2.1 게시글 작성 - 첨부파일 X]
    - [4.2.2 게시글 작성 - 첨부파일 O]
    - [4.2.3 게시글 페이지 조회 [카테고리]]
    - [4.2.4 게시글 페이지 조회]
    - [4.2.5 게시글 단건 조회 [카테고리]]
    - [4.2.6 게시글 단건 조회]
    - [4.2.7 게시글 수정]
    - [4.2.8 게시글 삭제]
  - [4.3 답글 API]
    - [4.3.1 답글 작성]
    - [4.3.2 답글 페이지 조회]
    - [4.3.3 답글 수정]
    - [4.3.4 답글 삭제]
  - [4.4 예외 사항]
    - [4.4.1 사용자 권한]
    - [4.4.2 관리자 권한]

***

## 1. 요구사항

- 해당 웹사이트는 카테고리에 따른 게시글로 이루어져 있다.
- 사용자는 게시글을 작성하기 위해서는 회원가입 후 로그인을 해야한다.
- 사용자는 답글을 작성하기 위해서는 회원가입 후 로그인을 해야한다.
- 회원 이름, 회원아이디, 닉네임, 비밀번호, 이메일을 갖는다.
- 회원은 게시글을 작성할 수 있고 답글을 남길 수 있다.
- 회원은 답글에도 답글을 남길 수 있다.
- 회원은 본인의 정보를 수정하거나 삭제(탈퇴)할 수 있다.
- 회원은 본인의 게시글과 답글을 수정하거나 삭제할 수 있다.
- 부모답글이 삭제되면 부모답글에 종속된 자식 답글들 또한 모두 삭제된다.
- 게시글은 제목, 내용, 작성자, 작성일, 최종수정일, 카테고리, 첨부파일(옵션)을 갖는다.
- 게시글 삭제시 게시글에 종속된 모든 답글은 삭제된다.
- 답글은 작성자, 내용, 작성일, 최종수정일을 갖는다.
- 첨부파일은 원본 파일이름, 서버에 저장될 파일이름, 파일형식, 파일사이즈를 갖는다.
- 하나의 게시글에 여러개의 파일을 첨부할 수 있다.
- 사용자는 여러개의 게시글을 작성할 수 있다.
- 사용자는 게시글에 여러 답글을 작성할 수 있다.
- 하나의 답글에는 여러개의 답글이 작성될 수 있다.

<br/>

## 2. 개념적 설계와 논리적 설계

### 개념적 스키마

![image](https://user-images.githubusercontent.com/60972528/127871503-f5f7d788-85ef-4d05-93f0-3332224dd5d2.png)

<br/>

### 논리적 스키마

![image](https://user-images.githubusercontent.com/60972528/127871550-d4ba44c3-a368-45de-ac61-686ce75da9ec.png)

<br/>

## 3. 테이블 명세

```sql
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
    name               varchar(10) NOT NULL,        --사용자명
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
```

<br/>

## 4. API

API 응답에 대한 공통 포맷 정의 

- 정상처리의 경우 리소스 생성은 **HTTP STATUS 201**로 응답
- 그 외의 정상처리는 모두 **HTTP STATUS 200**으로 응답
- 인증관련 오류는 **HTTP STATUS 401 또는 403**으로 응답
- 요청 매핑 핸들러가 없는 경우 **HTTP STATUS 404**로 응답
- 지원하지 않는 미디어 타입일 경우 **HTTP STATUS 415**로 응답
- 그 외의 클라이언트 오류 또는 서비스 예외의 경우 **HTTP STATUS 400**으로 응답
- 내부 서버 에러는 **HTTP STATUS 500**으로 응답
- 정상 처리의 경우 data 필드가 존재, error 필드는 null을 출력
- 오류 처리의 경우 data 필드는 null을 출력, error 필드는 오류 메시지를 출력

<br/>

## 4.1 사용자 API

### 4.1.1 사용자 회원가입

- URI : POST /api/join

- Request Body

  - ```json
    {
      "userId": "testId",
      "name": "testName",
      "nickName": "testNickName",
      "password": "1234",
      "email": "test@example.com"
    }
    ```

- Response Body

  - ```json
    {
        "data": {
            "email": "test@example.com",
            "name": "testName",
            "user_id": "testId",
            "nickname": "testNickName",
            "created_at": "2021-08-02T21:20:59.33839",
            "last_modified_at": "2021-08-02T21:20:59.33839"
        },
        "error": null
    }
    ```

<br/>

### 4.1.2 사용자 로그인

- URI : POST /api/login

- Request Body

  - ```json
    {
        "userId": "testId",
        "password": "1234"
    }
    ```

- Response Body

  - ```json
    {
        "data": {
            "email": "test@example.com",
            "name": "testName",
            "user_id": "testId",
            "nickname": "testNickName",
            "created_at": "2021-08-02T21:20:59.33839",
            "last_modified_at": "2021-08-02T21:20:59.33839"
        },
        "error": null
    }
    ```

<br/>

### 4.1.3 로그아웃

- URI : POST /api/logout

- Response Body

  - ```json
    {
        "data": "logout succeed",
        "error": null
    }
    ```

<br/>

### 4.1.4 모든 사용자 조회

- URI : GET /api/users

- Response Body

  - ```json
    {
        "data": [
            {
                "email": "sjsage522@gmail.com",
                "name": "junseok",
                "user_id": "admin",
                "nickname": "jun",
                "created_at": "2021-08-02T21:20:55.562741",
                "last_modified_at": "2021-08-02T21:20:55.562741"
            },
            {
                "email": "test@example.com",
                "name": "testName",
                "user_id": "testId",
                "nickname": "testNickName",
                "created_at": "2021-08-02T21:20:59.33839",
                "last_modified_at": "2021-08-02T21:20:59.33839"
            }
        ],
        "error": null
    }
    ```

<br/>

### 4.1.5 사용자 단건 조회

- URI : GET /api/users/testId

- Response Body

  - ```json
    {
        "data": {
            "email": "test@example.com",
            "name": "testName",
            "user_id": "testId",
            "nickname": "testNickName",
            "created_at": "2021-08-02T20:49:24.553276"
        },
        "error": null
    }
    ```

<br/>

### 4.1.6 사용자 정보 수정

- URI : PATCH /api/users/testId

- Request Body

  - ```json
    {
        "name": "update_testName",
        "nickName": "update_testNickName",
        "password": "update_1234"
    }
    ```

- Response Body

  - ```json
    {
        "data": {
            "email": "test@example.com",
            "name": "update_testName",
            "user_id": "testId",
            "nickname": "update_testNickName",
            "created_at": "2021-08-02T21:31:54.87854",
            "last_modified_at": "2021-08-02T21:31:59.555402"
        },
        "error": null
    }
    ```

<br/>

### 4.1.7 사용자 정보 삭제

- URI : DELETE /api/users/testId

- Response Body

  - ```json
    {
        "data": "testId: user is deleted successfully",
        "error": null
    }
    ```

<br/>

## 4.2 게시글 API

### 4.2.1 게시글 작성 - 첨부파일 X

- URI : POST /api/boards

- form-data

  - title
  - content
  - categoryName

- Response Body

  - ```json
    {
        "data": {
            "boardId": 1,
            "title": "title",
            "content": "content",
            "user_id": "testId",
            "category_name": "CODING",
            "upload_files": [],
            "created_at": "2021-08-02T21:54:29.550022",
            "last_modified_at": "2021-08-02T21:54:29.550022"
        },
        "error": null
    }
    ```

<br/>

### 4.2.2 게시글 작성 - 첨부파일 O

- URI : POST /api/boards
- form-data
  - title
  - content
  - categoryName
  - files
    ...
  - files

- Response Body

  - ```json
    {
        "data": {
            "boardId": 2,
            "title": "title",
            "content": "content",
            "user_id": "testId",
            "category_name": "CODING",
            "upload_files": [
                {
                    "uploadFileId": 1,
                    "originalFileName": "java.png",
                    "fileSize": 3413
                },
                {
                    "uploadFileId": 2,
                    "originalFileName": "java.png",
                    "fileSize": 3413
                }
            ],
            "created_at": "2021-08-02T21:05:23.941126",
            "last_modified_at": "2021-08-02T21:05:23.941126"
        },
        "error": null
    }
    ```

<br/>

### 4.2.3 게시글 페이지 조회 [카테고리]

- URI : GET /api/boards/category/coding?page=0

- Params

  - page

- Response Body

  - ```json
    {
        "data": [
            {
                "boardId": 2,
                "title": "title",
                "content": "content",
                "user_id": "testId",
                "category_name": "CODING",
                "upload_files": [],
                "created_at": "2021-08-02T21:59:50.727229",
                "last_modified_at": "2021-08-02T21:59:50.727229"
            },
            {
                "boardId": 1,
                "title": "title",
                "content": "content",
                "user_id": "testId",
                "category_name": "CODING",
                "upload_files": [],
                "created_at": "2021-08-02T21:54:29.550022",
                "last_modified_at": "2021-08-02T21:54:29.550022"
            }
        ],
        "error": null
    }
    ```

<br/>

### 4.2.4 게시글 페이지 조회

- URI : GET /api/boards?page=0

- Params

  - page

- Response Body

  - ```json
    {
        "data": [
            {
                "boardId": 4,
                "title": "title",
                "content": "content",
                "user_id": "testId",
                "category_name": "GAME",
                "upload_files": [
                    {
                        "uploadFileId": 3,
                        "originalFileName": "java.png",
                        "fileSize": 3413
                    },
                    {
                        "uploadFileId": 4,
                        "originalFileName": "java.png",
                        "fileSize": 3413
                    }
                ],
                "created_at": "2021-08-02T22:04:13.805136",
                "last_modified_at": "2021-08-02T22:04:13.805136"
            },
            {
                "boardId": 3,
                "title": "title",
                "content": "content",
                "user_id": "testId",
                "category_name": "CODING",
                "upload_files": [
                    {
                        "uploadFileId": 1,
                        "originalFileName": "java.png",
                        "fileSize": 3413
                    },
                    {
                        "uploadFileId": 2,
                        "originalFileName": "java.png",
                        "fileSize": 3413
                    }
                ],
                "created_at": "2021-08-02T22:03:54.517747",
                "last_modified_at": "2021-08-02T22:03:54.517747"
            },
            {
                "boardId": 2,
                "title": "title",
                "content": "content",
                "user_id": "testId",
                "category_name": "CODING",
                "upload_files": [],
                "created_at": "2021-08-02T21:59:50.727229",
                "last_modified_at": "2021-08-02T21:59:50.727229"
            },
            {
                "boardId": 1,
                "title": "title",
                "content": "content",
                "user_id": "testId",
                "category_name": "CODING",
                "upload_files": [],
                "created_at": "2021-08-02T21:54:29.550022",
                "last_modified_at": "2021-08-02T21:54:29.550022"
            }
        ],
        "error": null
    }
    ```

<br/>

### 4.2.5 게시글 단건 조회 [카테고리]

- URI : GET /api/boards/4/category/game

- Response Body

  - ```json
    {
        "data": {
            "boardId": 4,
            "title": "title",
            "content": "content",
            "user_id": "testId",
            "category_name": "GAME",
            "upload_files": [
                {
                    "uploadFileId": 3,
                    "originalFileName": "java.png",
                    "fileSize": 3413
                },
                {
                    "uploadFileId": 4,
                    "originalFileName": "java.png",
                    "fileSize": 3413
                }
            ],
            "created_at": "2021-08-02T22:04:13.805136",
            "last_modified_at": "2021-08-02T22:04:13.805136"
        },
        "error": null
    }
    ```

<br/>

### 4.2.6 게시글 단건 조회

- URI : GET /api/boards/3

- Response Body

  - ```json
    {
        "data": {
            "boardId": 3,
            "title": "title",
            "content": "content",
            "user_id": "testId",
            "category_name": "CODING",
            "upload_files": [
                {
                    "uploadFileId": 1,
                    "originalFileName": "java.png",
                    "fileSize": 3413
                },
                {
                    "uploadFileId": 2,
                    "originalFileName": "java.png",
                    "fileSize": 3413
                }
            ],
            "created_at": "2021-08-02T22:03:54.517747",
            "last_modified_at": "2021-08-02T22:03:54.517747"
        },
        "error": null
    }
    ```

<br/>

### 4.2.7 게시글 수정

- URI : PATCH /api/boards/1/category/coding

- Request Body

  - ```json
    {
        "title": "update_title",
        "content": "update_content",
        "categoryName": "game"
    }
    ```

- Response Body

  - ```json
    {
        "data": {
            "boardId": 1,
            "title": "update_title",
            "content": "update_content",
            "user_id": "testId",
            "category_name": "GAME",
            "upload_files": [],
            "created_at": "2021-08-02T21:54:29.550022",
            "last_modified_at": "2021-08-02T22:06:49.194746"
        },
        "error": null
    }
    ```

<br/>

### 4.2.8 게시글 삭제

- URI : DELETE /api/boards/1/category/game

- Response Body

  - ```json
    {
        "data": "board is deleted successfully",
        "error": null
    }
    ```

<br/>

## 4.3 답글 API

### 4.3.1 답글 작성

- URI : POST /api/comments/boards/2

- Request Body

  - ```json
    {
        "content": "답글"
    }
    ```

- Response Body

  - ```json
    {
        "data": {
            "parent_id": null,
            "comment_id": 1,
            "board_id": 2,
            "writer": "testId",
            "created_at": "2021-08-02T22:11:29.303768",
            "last_modified_at": "2021-08-02T22:11:29.303768",
            "content": "답글",
            "children": []
        },
        "error": null
    }
    ```

<br/>

- Request Body

  - ```json
    {
        "parentId": 1,
        "content": "답글"
    }
    ```

- Response Body

  - ```json
    {
        "data": {
            "parent_id": 1,
            "comment_id": 2,
            "board_id": 2,
            "writer": "testId",
            "created_at": "2021-08-02T22:12:37.014625",
            "last_modified_at": "2021-08-02T22:12:37.014625",
            "content": "답글",
            "children": []
        },
        "error": null
    }
    ```

<br/>

### 4.3.2 답글 페이지 조회

- URI : GET /api/comments/boards/2?page=0

- Params

  - page

- Response Body

  - ```json
    {
        "data": [
            {
                "parent_id": null,
                "comment_id": 1,
                "board_id": 2,
                "writer": "testId",
                "created_at": "2021-08-02T22:11:29.303768",
                "last_modified_at": "2021-08-02T22:11:29.303768",
                "content": "답글",
                "children": [
                    {
                        "parent_id": 1,
                        "comment_id": 2,
                        "board_id": 2,
                        "writer": "testId",
                        "created_at": "2021-08-02T22:12:37.014625",
                        "last_modified_at": "2021-08-02T22:12:37.014625",
                        "content": "답글",
                        "children": []
                    }
                ]
            }
        ],
        "error": null
    }
    ```

<br/>

### 4.3.3 답글 수정

- URI : PATCH /api/comments/1

- Request Body

  - ```json
    {
        "content": "update_content"
    }
    ```

- Response Body

  - ```json
    {
        "data": {
            "parent_id": null,
            "comment_id": 1,
            "board_id": 2,
            "writer": "testId",
            "created_at": "2021-08-02T22:11:29.303768",
            "last_modified_at": "2021-08-02T22:15:43.905283",
            "content": "update_content",
            "children": [
                {
                    "parent_id": 1,
                    "comment_id": 2,
                    "board_id": 2,
                    "writer": "testId",
                    "created_at": "2021-08-02T22:12:37.014625",
                    "last_modified_at": "2021-08-02T22:12:37.014625",
                    "content": "답글",
                    "children": []
                }
            ]
        },
        "error": null
    }
    ```

<br/>

### 4.3.4 답글 삭제

- URI : DELETE /api/comments/1

- Response Body

  - ```json
    {
        "data": "comment is deleted successfully",
        "error": null
    }
    ```

<br/>

- 1번 답글 삭제 후 답글 페이지 조회

  - ```json
    {
        "data": [],
        "error": null
    }
    ```

  - 부모 답글이 삭제 되었으므로, 자식 답글이 모두 삭제된다.

<br/>

## 4.4 예외 사항

### 4.4.1 사용자 권한

- AuthCheckInterceptor.java

  - 권한 분리 적용

- 조회를 제외한 요청들은 로그인 하지않으면 해당 요청을 처리하지 않음 (사용자 조회도 포함)

- URI : GET /api/users/testId

  - 로그인 하지 않았을경우

- Response Body

  - ```json
    {
        "data": null,
        "error": {
            "message": "로그인하지 않은 사용자입니다.",
            "status": 401
        }
    }
    ```

<br/>

- URI : POST /api/comments/boards/1

- Request Body

  - ```json
    {
        "content": "답글"
    }
    ```

- Response Body

  - ```json
    {
        "data": null,
        "error": {
            "message": "로그인하지 않은 사용자입니다.",
            "status": 401
        }
    }
    ```

<br/>

### 4.4.2 관리자 권한

- 모든 사용자 조회 API 는 관리자만 호출할 수 있다.

- URI : GET /api/users

  - 관리자가 아닌, 일반 사용자의 경우

- Response Body

  - ```json
    {
        "data": null,
        "error": {
            "message": "접근 권한이 없습니다.",
            "status": 403
        }
    }
    ```