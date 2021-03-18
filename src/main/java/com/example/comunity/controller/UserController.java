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
     * @param userJoinDto 회원가입 dto
     * @throws DuplicateUserIdException 이미 존재하는 id가 있는지 검사
     * @throws DuplicateUserNickNameException 이미 존재하는 별명이 있는지 검사
     */
    @PostMapping("/users")
    public ResponseEntity<EntityModel<UserDto>> join(@Valid @RequestBody final UserJoinDto userJoinDto)
            throws DuplicateUserIdException, DuplicateUserNickNameException {

        User newUser = userService.join(userJoinDto);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).join(userJoinDto)).toUri())
                .body(assembler.toModel(getUserResponseDto(newUser)));
    }

    /**
     * 로그인
     * @param userLoginDto 로그인 dto
     * @param session 헌재 사용자 세션
     * @throws NoMatchUserInfoException 아이디와 비밀번호가 유효한지 검사
     */
    @PostMapping("/login")
    public ResponseEntity<EntityModel<UserDto>> login(@Valid @RequestBody final UserLoginDto userLoginDto, final HttpSession session)
            throws NoMatchUserInfoException {
        User loginUser = userAuthService.authenticate(userLoginDto.getUserId(), userLoginDto.getPassword());

        session.setAttribute("authInfo", loginUser);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findById(userLoginDto.getUserId())).toUri())
                .body(EntityModel.of(getUserResponseDto(loginUser),
                        linkTo(methodOn(UserController.class).login(userLoginDto, session)).withSelfRel(),
                        linkTo(methodOn(UserController.class).findById(userLoginDto.getUserId())).withRel("id"),
                        linkTo(methodOn(UserController.class).findAll()).withRel("users")));
    }

    /**
     * 로그아웃
     * @param session 현재 사용자 세션
     */
    @GetMapping("/logout")
    public ResponseEntity<EntityModel<UserDto>> logout(final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");
        session.invalidate();

        UserLoginDto userLoginDto = new UserLoginDto(loginUser.getUserId(), loginUser.getNickName());
        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).login(userLoginDto, session)).toUri())
                .body(EntityModel.of(getUserResponseDto(loginUser),
                        linkTo(methodOn(UserController.class).logout(session)).withSelfRel(),
                        linkTo(methodOn(UserController.class).login(userLoginDto, session)).withRel("login")));
    }

    /**
     * 특정 사용자 조회
     * @param id 조회할 아이디
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<EntityModel<UserDto>> findById(@PathVariable final String id) {
        User findUser = userService.findById(id);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findAll()).toUri())
                .body(assembler.toModel(getUserResponseDto(findUser)));
    }

    /**
     * 모든 사용자 조회
     */
    @GetMapping("/users")
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> findAll() {
        List<EntityModel<UserDto>> users = new ArrayList<>();
        for (User user : userService.findAll()) {
            users.add(assembler.toModel(getUserResponseDto(user)));
        }

        return ResponseEntity.ok(CollectionModel.of(users,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel()
                        .andAffordance(afford(methodOn(UserController.class).join(null)))));
    }

    /**
     * 사용자 정보 수정
     * @param id 사용자 아이디
     * @param userUpdateDto 사용자 정보 수정 dto
     */
    @PatchMapping("/users/{id}")
    public ResponseEntity<EntityModel<UserDto>> update(@PathVariable final String id, @Valid @RequestBody final UserUpdateDto userUpdateDto) {
        User updatedUser = userService.update(id, userUpdateDto);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findById(id)).toUri())
                .body(assembler.toModel(getUserResponseDto(updatedUser)));
    }

    /**
     * It deletes based upon id then returns an HTTP 204 No Content response.
     * @return http 204 response
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<EntityModel<UserDto>> delete(@PathVariable final String id, @Valid @RequestBody final UserDeleteDto userDeleteDto, final HttpSession session) {
        userService.delete(id, userDeleteDto);

        session.invalidate();

        return ResponseEntity.noContent().build();
    }

    @Component
    public static class UserDtoModelAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {

        @Override
        public EntityModel<UserDto> toModel(final UserDto userDto) {

            return EntityModel.of(userDto,
                    linkTo(methodOn(UserController.class).findById(userDto.getUserId())).withSelfRel()
                            .andAffordance(afford(methodOn(UserController.class).update(userDto.getUserId(), null)))
                            .andAffordance(afford(methodOn(UserController.class).delete(userDto.getUserId(), null, null))),
                    linkTo(methodOn(UserController.class).findAll()).withRel("users"));
        }
    }

    private UserResponseDto getUserResponseDto(User newUser) {
        return new UserResponseDto(newUser);
    }
}
