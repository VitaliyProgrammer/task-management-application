package org.example.infrastructure.exception.global;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.example.domain.exception.ForbiddenActionException;
import org.example.domain.exception.LabelAlreadyExistsException;
import org.example.domain.exception.LabelNotFoundException;
import org.example.domain.exception.ProjectNotFoundException;
import org.example.domain.exception.RegistrationException;
import org.example.domain.exception.TaskNotFoundException;
import org.example.domain.exception.UserNotFoundException;
import org.example.domain.exception.UserRoleNotFoundException;
import org.example.infrastructure.exception.authentication.AuthenticationException;
import org.example.infrastructure.exception.authentication.ExpiredJwtException;
import org.example.infrastructure.exception.authentication.JwtException;
import org.example.infrastructure.exception.integration.DropBoxDownloadException;
import org.example.infrastructure.exception.integration.DropBoxFileNotFoundException;
import org.example.infrastructure.exception.integration.DropBoxUploadException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd 'T' HH:mm:ss");

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));

        List<String> fieldsOrderDto =
                ex.getBindingResult().getTarget() != null
                        ? Arrays.stream(ex.getBindingResult().getTarget()
                                .getClass().getDeclaredFields())
                        .map(Field::getName)
                        .toList()
                        : Collections.emptyList();

        List<String> errors =
                ex.getBindingResult().getAllErrors().stream()
                        .sorted(
                                Comparator.comparingInt(
                                        error -> {
                                            if (error instanceof FieldError fieldError) {
                                                return fieldsOrderDto.indexOf(
                                                        fieldError.getField());
                                            }
                                            return Integer.MAX_VALUE;
                                        }))
                        .map(this::getErrorMessage)
                        .toList();

        body.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private String getErrorMessage(ObjectError objectError) {
        return objectError.getDefaultMessage();
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Map<String, Object>> handleRegistrationException(
            RegistrationException registrationException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", registrationException.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException authenticationException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", authenticationException.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(
            UserNotFoundException userNotFoundException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", userNotFoundException.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UserRoleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserRoleNotFoundException(
            UserRoleNotFoundException userRoleNotFoundException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", userRoleNotFoundException.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProjectNotFoundException(
            ProjectNotFoundException projectNotFoundException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", projectNotFoundException.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaskNotFoundException(
            TaskNotFoundException taskNotFoundException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", taskNotFoundException.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(LabelNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleLabelNotFoundException(
            LabelNotFoundException labelNotFoundException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", labelNotFoundException.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(LabelAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleLabelAlreadyExistsException(
            LabelAlreadyExistsException labelAlreadyExistsException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", labelAlreadyExistsException.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DropBoxDownloadException.class)
    public ResponseEntity<Map<String, Object>> handleDropBoxDownloadException(
            DropBoxDownloadException dropBoxDownloadException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", dropBoxDownloadException.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(DropBoxUploadException.class)
    public ResponseEntity<Map<String, Object>> handleDropBoxUploadException(
            DropBoxUploadException dropBoxUploadException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", dropBoxUploadException.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(DropBoxFileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDropBoxFileNotFoundException(
            DropBoxFileNotFoundException dropBoxFileNotFoundException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", dropBoxFileNotFoundException.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenActionException(
            ForbiddenActionException forbiddenActionException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", forbiddenActionException.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleJwtIsValidOrNotExistsException(JwtException jwtException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", jwtException.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwtException(
            ExpiredJwtException expiredJwtException) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("error", expiredJwtException.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}
