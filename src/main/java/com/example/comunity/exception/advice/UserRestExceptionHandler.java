package com.example.comunity.exception.advice;

import com.example.comunity.exception.DuplicateUserIdException;
import com.example.comunity.exception.DuplicateUserNickNameException;
import com.example.comunity.exception.NoMatchUserInfoException;
import com.example.comunity.dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class UserRestExceptionHandler {

    @ExceptionHandler(DuplicateUserIdException.class)
    private ResponseEntity<Object> handleDuplicateUser(
            DuplicateUserIdException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(DuplicateUserNickNameException.class)
    private ResponseEntity<Object> handleDuplicateUser(
            DuplicateUserNickNameException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(NoMatchUserInfoException.class)
    protected ResponseEntity<Object> handleNoMatchUserInfo(
            NoMatchUserInfoException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

//    @ExceptionHandler
//    protected ResponseEntity<Object> exception(
//            Exception ex) {
//        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
//        apiError.setMessage(ex.getMessage());
//        return buildResponseEntity(apiError);
//    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
