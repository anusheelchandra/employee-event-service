package com.eventservice.exception.handler;

import com.eventservice.exception.EmployeeAttributeException;
import com.eventservice.exception.EntityNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

  private static final StringWriter STRING_WRITER = new StringWriter();

  @ExceptionHandler(EmployeeAttributeException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> employeeAttributeException(RuntimeException exception) {
    logErrorTrace(exception);
    return buildErrorResponseEntity("Wrong Employee input data", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> entityNotFoundException(RuntimeException exception) {
    logErrorTrace(exception);
    return buildErrorResponseEntity("Entity not found", HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> unknownException(Exception exception) {
    logErrorTrace(exception);
    return buildErrorResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> validationException(
      MethodArgumentNotValidException exception) {
    logErrorTrace(exception);
    return buildErrorResponseEntity("Wrong input data", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({
    ConstraintViolationException.class,
  })
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> constraintViolationException(
      ConstraintViolationException exception) {
    logErrorTrace(exception);
    return buildErrorResponseEntity("Wrong input data", HttpStatus.BAD_REQUEST);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponseEntity(
      String message, HttpStatus httpStatus) {
    return new ResponseEntity<>(
        ErrorResponse.builder()
            .message(message)
            .errorCode(httpStatus.value())
            .timestamp(LocalDateTime.now())
            .build(),
        httpStatus);
  }

  private void logErrorTrace(Exception exception) {
    exception.printStackTrace(new PrintWriter(STRING_WRITER));
    log.error(STRING_WRITER.toString());
  }
}
