package com.example.comunity.exception.advice;

import com.example.comunity.exception.*;
import com.example.comunity.dto.api.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(DuplicateUserIdException.class)
    private ResponseEntity<Object> handleDuplicateUser(
            final DuplicateUserIdException ex) {
        ApiError apiError = getApiError(HttpStatus.CONFLICT, ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(DuplicateUserNickNameException.class)
    private ResponseEntity<Object> handleDuplicateUser(
            final DuplicateUserNickNameException ex) {
        ApiError apiError = getApiError(HttpStatus.CONFLICT, ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(DuplicateCategoryNameException.class)
    protected ResponseEntity<Object> handleDuplicateCategoryName(
            final DuplicateCategoryNameException ex) {
        ApiError apiError = getApiError(HttpStatus.CONFLICT, ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(NoMatchUserInfoException.class)
    protected ResponseEntity<Object> handleNoMatchUserInfo(
            final NoMatchUserInfoException ex) {
        ApiError apiError = getApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(NoMatchBoardInfoException.class)
    protected ResponseEntity<Object> handleNoMatchBoardInfo(
            final NoMatchBoardInfoException ex) {
        ApiError apiError =
                getApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(NoMatchCategoryInfoException.class)
    protected ResponseEntity<Object> handleNoMatchCategoryInfo(
            final NoMatchCategoryInfoException ex) {
        ApiError apiError = getApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> methodArgumentNoValid(
            final MethodArgumentNotValidException ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage(ex
                .getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(final ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    private ApiError getApiError(HttpStatus notFound, String message) {
        ApiError apiError = new ApiError(notFound);
        apiError.setMessage(message);
        return apiError;
    }
}
