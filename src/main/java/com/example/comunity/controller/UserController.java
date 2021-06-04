package com.example.comunity.controller;

import com.example.comunity.domain.User;
import com.example.comunity.dto.user.*;
import com.example.comunity.exception.DuplicateUserIdException;
import com.example.comunity.exception.DuplicateUserNickNameException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.service.UserAuthService;
import com.example.comunity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;
    private final UserDtoModelAssembler assembler;

    /**
     * 회원가입
     * @param userJoinRequest 회원가입 dto
     * @throws DuplicateUserIdException 이미 존재하는 id가 있는지 검사
     * @throws DuplicateUserNickNameException 이미 존재하는 별명이 있는지 검사
     */
    @PostMapping("/join")
    public ResponseEntity<EntityModel<UserResponse>> join(@Valid @RequestBody final UserJoinRequest userJoinRequest)
            throws DuplicateUserIdException, DuplicateUserNickNameException {

        User newUser = userService.join(userJoinRequest);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).join(userJoinRequest)).toUri())
                .body(assembler.toModel(getUserResponse(newUser)));
    }

    /**
     * 로그인
     * @param userLoginRequest 로그인 dto
     * @param session 헌재 사용자 세션
     * @throws NoMatchUserInfoException 아이디와 비밀번호가 유효한지 검사
     */
    @PostMapping("/login")
    public ResponseEntity<EntityModel<UserResponse>> login(@Valid @RequestBody final UserLoginRequest userLoginRequest, final HttpSession session)
            throws NoMatchUserInfoException {
        User loginUser = userAuthService.authenticate(userLoginRequest.getUserId(), userLoginRequest.getPassword());

        session.setAttribute("authInfo", loginUser);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findById(userLoginRequest.getUserId())).toUri())
                .body(EntityModel.of(getUserResponse(loginUser),
                        linkTo(methodOn(UserController.class).login(userLoginRequest, session)).withSelfRel(),
                        linkTo(methodOn(UserController.class).findById(userLoginRequest.getUserId())).withRel("id"),
                        linkTo(methodOn(UserController.class).findAll()).withRel("users")));
    }

    /**
     * 로그아웃
     * @param session 현재 사용자 세션
     */
    @GetMapping("/logout")
    public ResponseEntity<EntityModel<UserResponse>> logout(final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");
        session.invalidate();

        UserLoginRequest userLoginRequest = new UserLoginRequest(loginUser.getUserId(), loginUser.getNickName());
        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).login(userLoginRequest, session)).toUri())
                .body(EntityModel.of(getUserResponse(loginUser),
                        linkTo(methodOn(UserController.class).logout(session)).withSelfRel(),
                        linkTo(methodOn(UserController.class).login(userLoginRequest, session)).withRel("login")));
    }

    /**
     * 특정 사용자 조회
     * @param userId 조회할 아이디
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<EntityModel<UserResponse>> findById(@PathVariable final String userId) {
        User findUser = userService.findById(userId);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findAll()).toUri())
                .body(assembler.toModel(getUserResponse(findUser)));
    }

    /**
     * 모든 사용자 조회
     */
    @GetMapping("/users")
    public ResponseEntity<CollectionModel<EntityModel<UserResponse>>> findAll() {
        List<EntityModel<UserResponse>> users = new ArrayList<>();
        for (User user : userService.findAll()) {
            users.add(assembler.toModel(getUserResponse(user)));
        }

        return ResponseEntity.ok(CollectionModel.of(users,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel()
                        .andAffordance(afford(methodOn(UserController.class).join(null)))));
    }

    /**
     * 사용자 정보 수정
     * @param userId 사용자 아이디
     * @param userUpdateRequest 사용자 정보 수정 dto
     */
    @PatchMapping("/users/{userId}")
    public ResponseEntity<EntityModel<UserResponse>> update(@PathVariable final String userId, @Valid @RequestBody final UserUpdateRequest userUpdateRequest, final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        User updatedUser = userService.update(userId, userUpdateRequest, loginUser);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findById(userId)).toUri())
                .body(assembler.toModel(getUserResponse(updatedUser)));
    }

    /**
     * It deletes based upon id then returns an HTTP 204 No Content response.
     * @return http 204 response
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<EntityModel<UserResponse>> delete(@PathVariable final String userId, @Valid @RequestBody final UserDeleteRequest userDeleteRequest, final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");

        userService.delete(userId, userDeleteRequest, loginUser);

        session.invalidate();

        return ResponseEntity.noContent().build();
    }

    @Component
    public static class UserDtoModelAssembler implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

        @Override
        public EntityModel<UserResponse> toModel(final UserResponse userResponse) {

            return EntityModel.of(userResponse,
                    linkTo(methodOn(UserController.class).findById(userResponse.getUserId())).withSelfRel()
                            .andAffordance(afford(methodOn(UserController.class).update(userResponse.getUserId(), null, null)))
                            .andAffordance(afford(methodOn(UserController.class).delete(userResponse.getUserId(), null, null))),
                    linkTo(methodOn(UserController.class).findAll()).withRel("users"));
        }
    }

    private UserResponse getUserResponse(User newUser) {
        return new UserResponse(newUser);
    }
}
