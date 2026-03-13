package no.nav.pdl.forvalter.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.exception.InternalServerException;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    private static final String GATEWAY_ORIGINAL_REQUEST_URL = "org.springframework.web.reactive.HandlerMapping.pathWithinHandlerMapping";

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRequestException.class)
    ExceptionInformation clientErrorException(ServerWebExchange serverWebExchange, InvalidRequestException exception) {
        return getExceptionInformation(serverWebExchange, exception);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    ExceptionInformation clientErrorException(ServerWebExchange serverWebExchange, NotFoundException exception) {
        return getExceptionInformation(serverWebExchange, exception);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerException.class)
    ExceptionInformation clientErrorException(ServerWebExchange serverWebExchange, InternalServerException exception) {
        return getExceptionInformation(serverWebExchange, exception);
    }

    private ExceptionInformation getExceptionInformation(ServerWebExchange serverWebExchange, HttpClientErrorException exception) {

        return ExceptionInformation.builder()
                .error(exception.getStatusText())
                .status(exception.getStatusCode().value())
                .message(exception.getStatusText())
                .path(serverWebExchange.getAttribute(GATEWAY_ORIGINAL_REQUEST_URL))
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