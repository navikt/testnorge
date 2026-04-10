package no.nav.testnav.apps.adresseservice.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExceptionInformation {

        private String message;
        private String error;
        private String path;
        private Integer status;
        private LocalDateTime timestamp;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    ExceptionInformation unhandledException(Exception exception, ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        log.error("Uventet feil på {}", path, exception);
        return ExceptionInformation.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(exception.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}