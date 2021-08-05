package com.example.comunity.controller.api;

import com.example.comunity.domain.Category;
import com.example.comunity.domain.User;
import com.example.comunity.repository.CategoryRepository;
import com.example.comunity.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import static com.example.comunity.util.JsonUtils.toJson;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test-pg")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
@DisplayName("API 테스트 (board)")
@DirtiesContext
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final MockHttpSession session = new MockHttpSession();

    @BeforeAll
    void init() {
        User user = User.of("testId", "testName", "testNickName", "1234", "email@example.com");
        Category codingCategory = Category.from("coding");
        Category gameCategory = Category.from("game");

        userRepository.save(user);
        categoryRepository.saveAll(List.of(codingCategory, gameCategory));

        session.setAttribute("authInfo", user);
    }

    @Test
    @DisplayName("[성공 테스트] 01. 게시글 작성 - 첨부파일 O")
    void _01_upload_succeed_test() throws Exception {
        FileInputStream fis = new FileInputStream("/Users/jun/Downloads/penguin.png");
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "files",
                "penguin.png",
                "image/png",
                fis
        );

        HashMap<String, String> contentTypeParams = new HashMap<>();
        contentTypeParams.put("boundary", "265001916915724");
        MediaType mediaType = new MediaType("multipart", "form-data", contentTypeParams);

        ResultActions result = mockMvc.perform(
                multipart("/api/boards")
                        .file(mockMultipartFile)
                        .param("title", "글제목")
                        .param("content", "글내용")
                        .param("categoryName", "CODING")
                        .contentType(mediaType)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title", is("글제목")))
                .andExpect(jsonPath("$.data.content", is("글내용")))
                .andExpect(jsonPath("$.data.category_name", is("CODING")))
                .andExpect(jsonPath("$.data.upload_files.length()", is(1)))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 02. 게시글 작성 - 첨부파일 X")
    void _02_upload_succeed_test() throws Exception {
        HashMap<String, String> contentTypeParams = new HashMap<>();
        contentTypeParams.put("boundary", "265001916915724");
        MediaType mediaType = new MediaType("multipart", "form-data", contentTypeParams);

        ResultActions result = mockMvc.perform(
                multipart("/api/boards")
                        .param("title", "글제목")
                        .param("content", "글내용")
                        .param("categoryName", "CODING")
                        .contentType(mediaType)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title", is("글제목")))
                .andExpect(jsonPath("$.data.content", is("글내용")))
                .andExpect(jsonPath("$.data.category_name", is("CODING")))
                .andExpect(jsonPath("$.data.upload_files.length()", is(0)))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 03. 게시글 작성 - 첨부파일 X")
    void _03_upload_succeed_test() throws Exception {
        HashMap<String, String> contentTypeParams = new HashMap<>();
        contentTypeParams.put("boundary", "265001916915724");
        MediaType mediaType = new MediaType("multipart", "form-data", contentTypeParams);

        ResultActions result = mockMvc.perform(
                multipart("/api/boards")
                        .param("title", "글제목")
                        .param("content", "글내용")
                        .param("categoryName", "game")
                        .contentType(mediaType)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title", is("글제목")))
                .andExpect(jsonPath("$.data.content", is("글내용")))
                .andExpect(jsonPath("$.data.category_name", is("GAME")))
                .andExpect(jsonPath("$.data.upload_files.length()", is(0)))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 04. 게시글 페이지 조회 - 카테고리")
    void _04_findAllWithCategory_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/boards/category/coding?page=0")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].boardId", is(2)))
                .andExpect(jsonPath("$.data[1].boardId", is(1)))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 05. 모든 게시글 페이지 조회")
    void _05_findAll_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/boards?page=0")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", is(3)))
                .andExpect(jsonPath("$.data[0].boardId", is(3)))
                .andExpect(jsonPath("$.data[1].boardId", is(2)))
                .andExpect(jsonPath("$.data[2].boardId", is(1)))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[실패 테스트] 06. 게시글 단건 조회 - 존재하지 않는 게시글")
    void _06_findByIdWithCategory_failed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/boards/1/category/game")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 07. 게시글 단건 조회(카테고리)")
    void _07_findByIdWithCategory_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/boards/1/category/coding")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_id", is("testId")))
                .andExpect(jsonPath("$.data.category_name", is("CODING")))
                .andExpect(jsonPath("$.data.upload_files.length()", is(1)))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 08. 게시글 단건 조회")
    void _08_findById_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/boards/3")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_id", is("testId")))
                .andExpect(jsonPath("$.data.category_name", is("GAME")))
                .andExpect(jsonPath("$.data.upload_files.length()", is(0)))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 09. 게시글 수정")
    void _09_updateBoard_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                patch("/api/boards/3/category/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("categoryName", "coding");
                                            }
                                        }
                                )
                        )
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_id", is("testId")))
                .andExpect(jsonPath("$.data.category_name", is("CODING")))
                .andExpect(jsonPath("$.data.upload_files.length()", is(0)))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 10. 게시글 삭제")
    void _10_deleteBoard_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                delete("/api/boards/3/category/coding")
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }
}