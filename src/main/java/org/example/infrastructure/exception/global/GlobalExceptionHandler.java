package org.example.infrastructure.exception.global;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Turns every error into one JSON shape (RFC 9457 "Problem Details"):
 * { "title", "status", "detail", "timestamp" } and, for validation, "errors": [...].
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String STORAGE_UNAVAILABLE =
            "File storage is temporarily unavailable. Please try again later.";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "Request validation failed");
        problem.setProperty("errors", sortedErrorMessages(exception));

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            UserRoleNotFoundException.class,
            ProjectNotFoundException.class,
            TaskNotFoundException.class,
            LabelNotFoundException.class,
            DropBoxFileNotFoundException.class})
    public ProblemDetail handleNotFound(RuntimeException exception) {

        return problem(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({RegistrationException.class, IllegalArgumentException.class})
    public ProblemDetail handleBadRequest(RuntimeException exception) {

        return problem(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler({
            AuthenticationException.class,
            JwtException.class,
            ExpiredJwtException.class})
    public ProblemDetail handleUnauthorized(RuntimeException exception) {

        return problem(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ProblemDetail handleForbidden(ForbiddenActionException exception) {

        return problem(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler({LabelAlreadyExistsException.class, IllegalStateException.class})
    public ProblemDetail handleConflict(RuntimeException exception) {

        return problem(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({DropBoxUploadException.class, DropBoxDownloadException.class})
    public ProblemDetail handleStorageFailure(RuntimeException exception) {

        log.error("Dropbox request failed", exception);

        return problem(HttpStatus.BAD_GATEWAY, STORAGE_UNAVAILABLE);
    }

    private ProblemDetail problem(HttpStatus status, String detail) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    private List<String> sortedErrorMessages(MethodArgumentNotValidException exception) {

        Object target = exception.getBindingResult().getTarget();

        List<String> fieldsInDtoOrder = target == null
                ? Collections.emptyList()
                : Arrays.stream(target.getClass().getDeclaredFields())
                        .map(Field::getName)
                        .toList();

        return exception.getBindingResult().getAllErrors().stream()
                .sorted(Comparator.comparingInt(error -> positionOf(error, fieldsInDtoOrder)))
                .map(ObjectError::getDefaultMessage)
                .toList();
    }

    private int positionOf(ObjectError error, List<String> fieldsInDtoOrder) {

        if (error instanceof FieldError fieldError) {
            return fieldsInDtoOrder.indexOf(fieldError.getField());
        }

        return Integer.MAX_VALUE;
    }
}
