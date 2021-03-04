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
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;
    private final UserDtoModelAssembler assembler;

    @PostMapping("/user")
    public ResponseEntity<EntityModel<UserDto>> join(@Valid @RequestBody final UserJoinDto userJoinDto)
            throws DuplicateUserIdException, DuplicateUserNickNameException {

        userService.join(userJoinDto);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).join(userJoinDto)).toUri())
                .body(EntityModel.of(userJoinDto,
                        linkTo(methodOn(UserController.class).findAll()).withSelfRel(),
                        linkTo(methodOn(UserController.class).login(new UserLoginDto(), null)).withRel("login")));
    }

    @PostMapping("/login")
    public ResponseEntity<EntityModel<UserDto>> login(@Valid @RequestBody final UserLoginDto userLoginDto, final HttpSession session)
            throws NoMatchUserInfoException {
        User loginUser = userAuthService.authenticate(userLoginDto.getUserId(), userLoginDto.getPassword());

        session.setAttribute("authInfo", loginUser);
        userLoginDto.setNickName(loginUser.getNickName());

        EntityModel<UserDto> model = assembler.toModel(userLoginDto);
        model.add(linkTo(methodOn(UserController.class).login(userLoginDto, session)).withSelfRel());

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findById(userLoginDto.getUserId())).toUri())
                .body(EntityModel.of(userLoginDto,
                        linkTo(methodOn(UserController.class).login(userLoginDto, session)).withSelfRel(),
                        linkTo(methodOn(UserController.class).findById(userLoginDto.getUserId())).withRel("id"),
                        linkTo(methodOn(UserController.class).findAll()).withRel("users")));
    }

    @GetMapping("/logout")
    public ResponseEntity<EntityModel<UserDto>> logout(final HttpSession session) {
        User loginUser = (User) session.getAttribute("authInfo");
        session.invalidate();

        UserLoginDto userLoginDto = new UserLoginDto(loginUser.getUserId(), loginUser.getNickName());
        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).login(userLoginDto, session)).toUri())
                .body(EntityModel.of(userLoginDto,
                        linkTo(methodOn(UserController.class).logout(session)).withSelfRel(),
                        linkTo(methodOn(UserController.class).login(userLoginDto, session)).withRel("login")));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<EntityModel<UserDto>> findById(@PathVariable final String id) {
        User findUser = userService.findById(id);

        UserDto userDto = new UserDto(findUser.getUserId(), findUser.getName(), findUser.getNickName(), findUser.getEmail());

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findById(id)).toUri())
                .body(EntityModel.of(userDto,
                        linkTo(methodOn(UserController.class).findById(id)).withSelfRel(),
                        linkTo(methodOn(UserController.class).logout(null)).withRel("logout"),
                        linkTo(methodOn(UserController.class).findAll()).withRel("users")));
    }

    @GetMapping("/users")
    public CollectionModel<EntityModel<UserDto>> findAll() {
        List<EntityModel<UserDto>> users = new ArrayList<>();
        for (User user : userService.findAll()) {
            UserDto userDto = new UserDto(user.getUserId(), user.getName(), user.getNickName(), user.getEmail());
            EntityModel<UserDto> userDtoEntityModel = assembler.toModel(userDto);
            users.add(userDtoEntityModel);
        }

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel());
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<EntityModel<UserDto>> update(@PathVariable final String id, @Valid @RequestBody final UserUpdateDto userUpdateDto) {
        userService.update(id, userUpdateDto);

        userUpdateDto.setUserId(id);
        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findById(id)).toUri())
                .body(assembler.toModel(userUpdateDto));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<EntityModel<UserDto>> delete(@PathVariable final String id, @Valid @RequestBody final UserDeleteDto userDeleteDto) {
        userService.delete(id, userDeleteDto);
        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).findById(id)).toUri())
                .body(EntityModel.of(userDeleteDto,
                        linkTo(methodOn(UserController.class).delete(id, userDeleteDto)).withSelfRel(),
                        linkTo(methodOn(UserController.class).join(new UserJoinDto())).withRel("join")));
    }

    @Component
    public static class UserDtoModelAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {

        @Override
        public EntityModel<UserDto> toModel(final UserDto userDto) {

            return EntityModel.of(userDto,
                    linkTo(methodOn(UserController.class).findById(userDto.getUserId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).findAll()).withRel("users"));
        }
    }
}
