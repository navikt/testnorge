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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionAdvice {

    private ExceptionInformation getExceptionInformation(ServerWebExchange request, HttpClientErrorException exception) {

        return ExceptionInformation.builder()
                .error(exception.getStatusCode().getReasonPhrase())
                .status(exception.getStatusCode().value())
                .message(exception.getStatusText())
                .path(request.getRequest().getPath().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRequestException.class)
    public ExceptionInformation clientErrorException(ServerWebExchange request, InvalidRequestException exception) {
        return getExceptionInformation(request, exception);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ExceptionInformation clientErrorException(ServerWebExchange request, NotFoundException exception) {
        return getExceptionInformation(request, exception);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerException.class)
    public ExceptionInformation clientErrorException(ServerWebExchange request, InternalServerException exception) {
        return getExceptionInformation(request, exception);
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