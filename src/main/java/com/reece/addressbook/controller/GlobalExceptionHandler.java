package com.reece.addressbook.controller;

import com.reece.addressbook.exception.InvalidHeaderContentException;
import com.reece.addressbook.exception.MandatoryHeaderMissingException;
import com.reece.addressbook.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.reece.addressbook.controller.ProblemDetailBuilder.builder;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String FIELDS = "fields";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {

        return builder()
                .status(HttpStatus.BAD_REQUEST)
                .detail(exception.getBody().getDetail())
                .title(exception.getBody().getTitle())
                .type(exception.getBody().getType())
                .property(FIELDS, getErrorProperties(exception))
                .build();
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handlerValidationException(ValidationException exception) {
        return builder()
                .status(HttpStatus.BAD_REQUEST)
                .detail(exception.getCause().getMessage())
                .build();
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handlerServiceException(ServiceException exception) {
        return builder()
                .status(HttpStatus.BAD_REQUEST)
                .detail(exception.getMessage())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handlerDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return builder()
                .status(HttpStatus.BAD_REQUEST)
                .detail("Duplicate entity")
                .build();
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, Exception.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handlerException(Exception exception) {
        return builder()
                .status(HttpStatus.BAD_REQUEST)
                .detail(exception.getMessage())
                .build();
    }

    private Map<String, Object> getErrorProperties(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getAllErrors().stream()
                .map(error -> new AbstractMap.SimpleImmutableEntry<>(((FieldError) error).getField(), error.getDefaultMessage()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
