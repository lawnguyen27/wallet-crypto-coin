package vn.sparkminds.template.config.error;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.sparkminds.template.config.error.http.BadRequestException;
import vn.sparkminds.template.config.error.http.NotFoundException;
import vn.sparkminds.template.config.message.MessageSourceService;

import static vn.sparkminds.template.constant.ErrorConstants.*;
//import static vn.sparkminds.template.constant.ErrorConstants.ERROR_ACCESS_DENIED;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionTranslator  {

    private final MessageSourceService messageService;

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NoResourceFoundException ex) {
        ProblemDetailWithCause problem = new ProblemDetailWithCause();
        problem.setCode(ERROR_RESOURCE_NOT_FOUND);
        problem.setMessage(messageService.getMessage(ERROR_RESOURCE_NOT_FOUND));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        ProblemDetailWithCause problem = new ProblemDetailWithCause();
        problem.setCode(ex.getMessage());
        problem.setMessage(messageService.getMessage(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetailWithCause problem = new ProblemDetailWithCause();
        problem.setCode(ERROR_INVALID_REQUEST);
        problem.setMessage(messageService.getMessage(ERROR_INVALID_REQUEST));
        problem.setFieldErrors(ex.getBindingResult().getFieldErrors().stream().map(ProblemDetailWithCause.FieldError::new).toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getClass().getName(), ex);
        ProblemDetailWithCause problem = new ProblemDetailWithCause();
        problem.setCode(ERROR_INTERNAL_SERVER_ERROR);
        problem.setMessage(messageService.getMessage(ERROR_INTERNAL_SERVER_ERROR));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.warn("AuthorizationDeniedException: {}", ex.getMessage());

        ProblemDetailWithCause problem = new ProblemDetailWithCause();
        problem.setCode("access.denied");
        problem.setMessage("access.denied");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

}
