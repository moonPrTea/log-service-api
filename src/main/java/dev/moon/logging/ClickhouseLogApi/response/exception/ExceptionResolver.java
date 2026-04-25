package dev.moon.logging.ClickhouseLogApi.response.exception;


import dev.moon.logging.ClickhouseLogApi.dto.ErrorAnswer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionResolver {

  // TODO: add error answer message for all exceptions
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorAnswer> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
    return ResponseEntity
            .badRequest()
            .body(new ErrorAnswer("Not valid data"));
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorAnswer> handleInternalServerError(
          Exception exception, HttpServletRequest request) {
    return ResponseEntity
            .internalServerError()
            .body(new ErrorAnswer("Internal server error at " + request.getRequestURI() + " route"));
  }

}
