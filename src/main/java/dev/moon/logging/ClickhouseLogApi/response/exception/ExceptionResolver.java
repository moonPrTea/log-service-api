package dev.moon.logging.ClickhouseLogApi.response.exception;


import dev.moon.logging.ClickhouseLogApi.dto.ErrorAnswer;
import org.springframework.http.HttpStatus;
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
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorAnswer("Not valid data"));
  }
}
