package com.example.comunity.controller;

import com.example.comunity.domain.User;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@DisplayName("API 테스트 (user)")
@DirtiesContext
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final Logger log = LoggerFactory.getLogger(UserControllerTest.class);
    private final MockHttpSession session = new MockHttpSession();

    @Test
    @DisplayName("[성공 테스트] 01. 사용자 회원가입")
    void _01_user_join_test_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("userId", "testId");
                                                put("name", "testName");
                                                put("nickName", "testNickName");
                                                put("password", "1234");
                                                put("email", "email@example.com");
                                            }
                                        }
                                )
                        )

        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.user_id", is("testId")))
                .andExpect(jsonPath("$.data.name", is("testName")))
                .andExpect(jsonPath("$.data.nickname", is("testNickName")))
                .andExpect(jsonPath("$.data.email", is("email@example.com")))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 02. 사용자 로그인")
    void _02_user_login_test_succeed_test() throws Exception {
        User user = User.of("testId", "testName", "testNickName", "1234", "email@example.com");
        session.setAttribute("authInfo", user);

        ResultActions result = mockMvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("userId", "testId");
                                                put("password", "1234");
                                            }
                                        }
                                )
                        )
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_id", is("testId")))
                .andExpect(jsonPath("$.data.name", is("testName")))
                .andExpect(jsonPath("$.data.nickname", is("testNickName")))
                .andExpect(jsonPath("$.data.email", is("email@example.com")))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[실패 테스트] 03. 사용자 로그인 - 비밀번호 불일치")
    void _03_user_login_failed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("userId", "testId");
                                                put("password", "1111");
                                            }
                                        }
                                )
                        )
        );

        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
        ;
    }

    @Test
    @DisplayName("[실패 테스트] 04. 사용자 로그인 - 아이디 불일치")
    void _04_user_login_failed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("userId", "unknown");
                                                put("password", "1234");
                                            }
                                        }
                                )
                        )
        );

        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 05. 다른 사용자 회원가입")
    void _05_otherUser_login_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("userId", "otherUser");
                                                put("name", "other");
                                                put("nickName", "otherNickName");
                                                put("password", "1234");
                                                put("email", "other@example.com");
                                            }
                                        }
                                )
                        )
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.user_id", is("otherUser")))
                .andExpect(jsonPath("$.data.name", is("other")))
                .andExpect(jsonPath("$.data.nickname", is("otherNickName")))
                .andExpect(jsonPath("$.data.email", is("other@example.com")))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 06. 다른 사용자 로그인")
    void _06_user_login_failed_test() throws Exception {
        User user = User.of("otherUser", "other", "otherNickName", "1234", "other@example.com");
        session.setAttribute("authInfo", user);

        ResultActions result = mockMvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("userId", "otherUser");
                                                put("password", "1234");
                                            }
                                        }
                                )
                        )
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_id", is("otherUser")))
                .andExpect(jsonPath("$.data.name", is("other")))
                .andExpect(jsonPath("$.data.nickname", is("otherNickName")))
                .andExpect(jsonPath("$.data.email", is("other@example.com")))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 07. 모든 사용자 조회")
    void _07_findAll_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(session)
        );

        final User authInfo = (User) session.getAttribute("authInfo");
        log.info("current user info = {}", authInfo);

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].user_id", is("testId")))
                .andExpect(jsonPath("$.data[1].user_id", is("otherUser")))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[성공 테스트] 08. 사용자 단건 조회")
    void _08_findById_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/users/testId")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_id", is("testId")))
                .andExpect(jsonPath("$.data.name", is("testName")))
                .andExpect(jsonPath("$.data.nickname", is("testNickName")))
                .andExpect(jsonPath("$.data.email", is("email@example.com")))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[실패 테스트] 09. 사용자 단건 조회 - 존재하지 않는 사용자")
    void _09_findById_failed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/users/unknown")
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
    @DisplayName("[실패 테스트] 10. 사용자 정보 수정 - 다른 사용자의 정보는 수정할 수 없음")
    void _10_update_failed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                patch("/api/users/testId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("name", "UPDATE_NAME");
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
    @DisplayName("[성공 테스트] 11. 사용자 정보 수정 - 이름,별명 수정")
    void _11_update_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                patch("/api/users/otherUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("name", "UPDATE_NAME");
                                                put("nickName", "UPDATE_NICKNAME");
                                            }
                                        }
                                )
                        )
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_id", is("otherUser")))
                .andExpect(jsonPath("$.data.name", is("UPDATE_NAME")))
                .andExpect(jsonPath("$.data.nickname", is("UPDATE_NICKNAME")))
                .andExpect(jsonPath("$.data.email", is("other@example.com")))
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[실패 테스트] 12. 사용자 정보 삭제 - 다른 사용자의 정보는 삭제할 수 없음")
    void _12_delete_failed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                delete("/api/users/testId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("userId", "testId");
                                                put("password", "1234");
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
    @DisplayName("[성공 테스트] 13. 사용자 정보 삭제")
    void _13_delete_succeed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                delete("/api/users/otherUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("userId", "otherUser");
                                                put("password", "1234");
                                            }
                                        }
                                )
                        )
                        .session(session)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.error").doesNotExist())
        ;
    }

    @Test
    @DisplayName("[실패 테스트] 14. 사용자 로그인 - 아이디 불일치(삭제된 사용자)")
    void _14_user_login_failed_test() throws Exception {
        ResultActions result = mockMvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                toJson(
                                        new HashMap<>() {
                                            {
                                                put("userId", "otherUser");
                                                put("password", "1234");
                                            }
                                        }
                                )
                        )
        );

        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
        ;
    }
}
