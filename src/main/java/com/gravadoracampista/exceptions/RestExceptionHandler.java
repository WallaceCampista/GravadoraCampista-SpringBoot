package com.gravadoracampista.exceptions;

import com.gravadoracampista.service.exceptions.allExceptions.CreateEntitiesException;
import com.gravadoracampista.service.exceptions.allExceptions.RestErrorMessage;
import com.gravadoracampista.service.exceptions.userExceptions.PasswordNotMatchException;
import com.gravadoracampista.utils.FormatList;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleSecurityException(RuntimeException ex) {

        ProblemDetail errorDetail = null;

        if (ex instanceof BadCredentialsException) {
            errorDetail = ProblemDetail
                    .forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("access_denied_reason", "Autenticação falhada!");
        }

        if (ex instanceof AccessDeniedException) {
            errorDetail = ProblemDetail
                    .forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("messageError", "Usuário não autenticado!");
            log.error(ex.getMessage(), ex);
        }

        if (ex instanceof JWTVerificationException) {
            errorDetail = ProblemDetail
                    .forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("access_denied_reason", "Usuário não autenticado!");
        }

        if (ex instanceof UnauthorizedException) {
            errorDetail = ProblemDetail
                    .forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
            errorDetail.setProperty("access_denied_reason", "Usuário não autorizado!");
            log.error(ex.getMessage(), ex);
        }
        return errorDetail;
    }

    @ExceptionHandler(CreateEntitiesException.class)
    private ResponseEntity<List<String>> createUserErroHandler(CreateEntitiesException createEntitiesException) {
        RestErrorMessage response = new RestErrorMessage(HttpStatus.BAD_REQUEST, createEntitiesException.getMessage());
        log.error("", createEntitiesException);
        var errorsList = FormatList.deleteBrackets(response.getMessage());
        return ResponseEntity.status(response.getStatus()).body(errorsList);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    private ResponseEntity<String> passwordNotMatchErroHandler(PasswordNotMatchException passwordNotMatchException) {
        RestErrorMessage response = new RestErrorMessage(HttpStatus.BAD_REQUEST, passwordNotMatchException.getMessage());
        log.error("", passwordNotMatchException);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    private ResponseEntity<String> notFoundErroHandler(ChangeSetPersister.NotFoundException notFoundException) {
        RestErrorMessage response = new RestErrorMessage(HttpStatus.NOT_FOUND, notFoundException.getMessage());
        log.error("", notFoundException);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<String> badRequestErroHandler(BadRequestException badRequestException) {
        RestErrorMessage response = new RestErrorMessage(HttpStatus.BAD_REQUEST, badRequestException.getMessage());
        log.error("", badRequestException);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NO_CONTENT);
    }
}