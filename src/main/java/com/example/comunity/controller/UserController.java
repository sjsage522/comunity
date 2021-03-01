package com.example.comunity.controller;

import com.example.comunity.domain.User;
import com.example.comunity.dto.user.UserDto;
import com.example.comunity.dto.user.UserJoinDto;
import com.example.comunity.dto.user.UserLoginDto;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;
    private final UserModelAssembler assembler;

    @PostMapping("/users")
    public ResponseEntity<EntityModel<UserDto>> join(@Valid @RequestBody final UserJoinDto userJoinDto)
            throws DuplicateUserIdException, DuplicateUserNickNameException {

        userService.join(userJoinDto);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findById(userJoinDto.getUserId())).toUri())
                .body(assembler.toModel(userJoinDto));
    }

    @PostMapping("/login")
    public ResponseEntity<EntityModel<UserDto>> login(@Valid @RequestBody final UserLoginDto userLoginDto, final HttpSession session)
            throws NoMatchUserInfoException {
        User loginUser = userAuthService.authenticate(userLoginDto.getUserId(), userLoginDto.getPassword());

        session.setAttribute("authInfo", loginUser);
        userLoginDto.setNickName(loginUser.getNickName());

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findById(userLoginDto.getUserId())).toUri())
                .body(assembler.toModel(userLoginDto));
    }

    @GetMapping("/logout")
    public void logout(final HttpSession session) {
        session.invalidate();
    }

    @GetMapping("/users/{id}")
    public EntityModel<UserDto> findById(@PathVariable String id) {
        User findUser = userService.findById(id);

        return assembler.toModel(new UserDto(findUser.getUserId(), findUser.getName(), findUser.getNickName(), findUser.getEmail()));
    }

    @GetMapping("/users")
    public CollectionModel<EntityModel<UserDto>> findAll() {
        List<EntityModel<UserDto>> users =
                userService.findAll().stream()
                        .map(user -> new UserDto(user.getUserId(), user.getName(), user.getNickName(), user.getEmail()))
                        .map(assembler::toModel)
                        .collect(Collectors.toList());
        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel());
    }

    @Component
    static class UserModelAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {

        @Override
        public EntityModel<UserDto> toModel(UserDto userDto) {

            EntityModel<UserDto> userDtoModel = EntityModel.of(userDto,
                    linkTo(methodOn(UserController.class).findById(userDto.getUserId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).findAll()).withRel("users"));

            // TODO if statement...

            return userDtoModel;
        }
    }
}
