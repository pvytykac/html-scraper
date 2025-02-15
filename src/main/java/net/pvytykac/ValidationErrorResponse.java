package net.pvytykac;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.Objects;

/**
 * @author Paly
 * @since 2025-02-15
 */
@Slf4j
@ControllerAdvice
public class ValidationErrorResponse {

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse validationError(HandlerMethodValidationException exception) {
        var errors = exception.getAllErrors().stream()
                .map(err -> {
                    var fieldName = ((DefaultMessageSourceResolvable) Objects.requireNonNull(err.getArguments())[0]).getDefaultMessage();
                    var errorMessage = err.getDefaultMessage();
                    return new Error(fieldName, errorMessage);
                }).toList();

        return new ErrorResponse(errors);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Value
    public static class ErrorResponse {
        List<Error> errors;
    }

    @Value
    public static class Error {
        String field;
        String message;
    }
}
