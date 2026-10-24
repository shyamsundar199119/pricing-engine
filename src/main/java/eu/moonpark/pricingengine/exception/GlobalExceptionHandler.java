package eu.moonpark.pricingengine.exception;

import eu.moonpark.pricingengine.dto.ErrorResponse;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidParkingRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParkingRequest(
            InvalidParkingRequestException exception) {

        log.warn("Invalid parking request: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(List.of(exception.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .toList();
        log.warn("Request validation error: {}", errors);

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(errors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException exception) {

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String message = error.getDefaultMessage();

                    return message != null
                            ? error.getField() + ": " + message
                            : "Invalid value for field: " + error.getField();
                })
                .distinct()
                .toList();

        log.warn("Request parameter binding error: {}", errors);

        if (errors.isEmpty()) {
            errors = List.of("Invalid request parameters");
        }

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {

        String message = "Invalid value for parameter: "
                + exception.getName();
        log.warn("Request parameter type mismatch: {}", message);

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(List.of(message)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception exception) {
        log.error("Unexpected error occurred", exception);
        String message = "Unexpected error occurred";

        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(List.of(message)));
    }
}


