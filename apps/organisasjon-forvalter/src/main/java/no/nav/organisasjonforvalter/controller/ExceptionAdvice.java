package no.nav.organisasjonforvalter.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionAdvice {

        @ResponseBody
        @ExceptionHandler(ResponseStatusException.class)
        ExceptionInformation clientErrorException(ResponseStatusException exception, ServerWebExchange exchange) {
            exchange.getResponse().setStatusCode(exception.getStatusCode());
            return ExceptionInformation.builder()
                    .error(exception.getReason())
                    .status(exception.getStatusCode().value())
                    .message(exception.getMessage())
                    .path(exchange.getRequest().getURI().getPath())
                    .timestamp(LocalDateTime.now())
                    .build();
        }

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
}
