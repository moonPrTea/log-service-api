package dev.moon.logging.ClickhouseLogApi.response.exception;


import dev.moon.logging.ClickhouseLogApi.dto.ErrorAnswer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionResolver {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorAnswer> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
    return ResponseEntity
            .badRequest()
            .body(new ErrorAnswer("Not valid data"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorAnswer> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
    String errorParam = exception.getPropertyName();

    if ("response_time".equals(errorParam)) {
      return ResponseEntity
              .badRequest()
              .body(new ErrorAnswer("response_time must be an Integer value"));
    }

    return ResponseEntity
            .badRequest()
            .body(new ErrorAnswer("Invalid param: " + errorParam));
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorAnswer> handleInternalServerError(
          Exception exception, HttpServletRequest request) {
    return ResponseEntity
            .internalServerError()
            .body(new ErrorAnswer("Internal server error at " + request.getRequestURI() + " route"));
  }

}
