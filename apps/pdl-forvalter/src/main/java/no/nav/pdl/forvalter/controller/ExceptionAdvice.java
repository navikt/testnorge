package no.nav.pdl.forvalter.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.exception.InternalServerException;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRequestException.class)
    public ExceptionInformation clientErrorException(InvalidRequestException exception, ServerWebExchange exchange) {
        return getExceptionInformation(HttpStatus.BAD_REQUEST, exception.getStatusText(), exchange);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ExceptionInformation clientErrorException(NotFoundException exception, ServerWebExchange exchange) {
        return getExceptionInformation(HttpStatus.NOT_FOUND, exception.getStatusText(), exchange);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerException.class)
    public ExceptionInformation clientErrorException(InternalServerException exception, ServerWebExchange exchange) {
        return getExceptionInformation(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusText(), exchange);
    }

    private ExceptionInformation getExceptionInformation(HttpStatus status, String message, ServerWebExchange exchange) {

        return ExceptionInformation.builder()
                .error(message)
                .status(status.value())
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ExceptionInformation {

        private String message;
        private String error;
        private String path;
        private Integer status;
        private LocalDateTime timestamp;
    }
}