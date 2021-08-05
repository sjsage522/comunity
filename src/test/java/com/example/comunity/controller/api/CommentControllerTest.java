package com.example.comunity.controller.api;

import com.example.comunity.domain.Board;
import com.example.comunity.domain.Category;
import com.example.comunity.domain.User;
import com.example.comunity.repository.BoardRepository;
import com.example.comunity.repository.CategoryRepository;
import com.example.comunity.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;

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
@DisplayName("API 테스트 (comment)")
@DirtiesContext
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BoardRepository boardRepository;

    private final MockHttpSession session = new MockHttpSession();

    @BeforeAll
    void init() {
        User user = User.of("testId", "testName", "testNickName", "1234", "email@example.com");
        Category codingCategory = Category.from("coding");
        Board board = Board.of(user, codingCategory, "board", "board");

        userRepository.save(user);
        categoryRepository.save(codingCategory);
        boardRepository.save(board);

        session.setAttribute("authInfo", user);
    }

    @Test
    @DisplayName("[성공 테스트] 01. 답글 작성")
    void _01_applyComment_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                post("/api/comments/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("content", "답글1");
                                            }
                                        }
                                )
                        )
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.board_id", is(1)))
                .andExpect(jsonPath("$.data.writer", is("testId")))
                .andExpect(jsonPath("$.data.content", is("답글1")))
                .andExpect(jsonPath("$.data.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 02. 답글 작성 - 자식답글")
    void _02_applyComment_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                post("/api/comments/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("parentId", 1L);
                                                put("content", "답글2");
                                            }
                                        }
                                )
                        )
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.board_id", is(1)))
                .andExpect(jsonPath("$.data.writer", is("testId")))
                .andExpect(jsonPath("$.data.content", is("답글2")))
                .andExpect(jsonPath("$.data.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 03. 답글 작성 - 다른답글")
    void _03_applyComment_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                post("/api/comments/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("content", "답글3");
                                            }
                                        }
                                )
                        )
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.board_id", is(1)))
                .andExpect(jsonPath("$.data.writer", is("testId")))
                .andExpect(jsonPath("$.data.content", is("답글3")))
                .andExpect(jsonPath("$.data.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 04. 답글 페이지 조회")
    void _04_findAll_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/comments/boards/1?page=0")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].parent_id").doesNotExist())
                .andExpect(jsonPath("$.data[0].comment_id", is(1)))
                .andExpect(jsonPath("$.data[0].content", is("답글1")))
                .andExpect(jsonPath("$.data[0].children.length()", is(1)))
                .andExpect(jsonPath("$.data[0].children[0].parent_id", is(1)))
                .andExpect(jsonPath("$.data[0].children[0].comment_id", is(2)))
                .andExpect(jsonPath("$.data[0].children[0].content", is("답글2")))
                .andExpect(jsonPath("$.data[1].parent_id").doesNotExist())
                .andExpect(jsonPath("$.data[1].comment_id", is(3)))
                .andExpect(jsonPath("$.data[1].content", is("답글3")))
                .andExpect(jsonPath("$.data[1].children.length()", is(0)))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 05. 답글 수정")
    void _05_update_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                patch("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("content", "UPDATE_CONTENT");
                                            }
                                        }
                                )
                        )
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", is("UPDATE_CONTENT")))
                .andExpect(jsonPath("$.data.children.length()", is(1)))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[실패 테스트] 06. 답글 수정 - 존재하지 않는 답글")
    void _06_update_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                patch("/api/comments/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("content", "UPDATE_CONTENT");
                                            }
                                        }
                                )
                        )
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 07. 답글 삭제")
    void _07_deleteComment_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                delete("/api/comments/1")
                        .session(session)
        );

        result.andDo(print())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 08. 답글 페이지 조회 - 삭제 후 조회")
    void _08_findAll_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/comments/boards/1?page=0")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].comment_id", is(3)))
                .andExpect(jsonPath("$.data[0].children.length()", is(0)))
                .andExpect(jsonPath("$.data[0].content", is("답글3")))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }
}
