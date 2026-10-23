package eu.moonpark.pricingengine.exception;

import eu.moonpark.pricingengine.dto.ErrorResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidParkingRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParkingRequest(
            InvalidParkingRequestException exception) {

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

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(List.of(message)));
    }
}


