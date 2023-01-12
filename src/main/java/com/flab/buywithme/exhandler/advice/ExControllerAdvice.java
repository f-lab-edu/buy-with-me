package com.flab.buywithme.exhandler.advice;

import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.flab.buywithme.controller")
public class ExControllerAdvice {

    @Autowired
    private MessageSource ms;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String processValidationEx(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder builder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(ms.getMessage(fieldError, LocaleContextHolder.getLocale()));
            builder.append(System.getProperty("line.separator"));
            builder.append("입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
            builder.append(System.getProperty("line.separator"));
        }

        return builder.toString();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleLoginIdDuplicateException() {
        return "이미 사용중인 아이디입니다";
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }

}
