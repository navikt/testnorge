package no.nav.pdl.forvalter.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.exception.InternalServerException;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
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
    @ExceptionHandler({InternalServerException.class, IllegalStateException.class})
    ExceptionInformation clientErrorException(ServerWebExchange serverWebExchange, InternalServerException exception) {
        return getExceptionInformation(serverWebExchange, exception);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(OptimisticLockingFailureException.class)
    ExceptionInformation optimisticLockingFailureException(
            ServerWebExchange serverWebExchange,
            OptimisticLockingFailureException exception) {

        log.warn("Samtidig oppdatering oppdaget for {}", serverWebExchange.getRequest().getPath().value(), exception);
        return ExceptionInformation.builder()
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .status(HttpStatus.CONFLICT.value())
                .message("Personen ble endret av en annen operasjon. Forsøk på nytt.")
                .path(serverWebExchange.getRequest().getPath().value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private ExceptionInformation getExceptionInformation(ServerWebExchange serverWebExchange, HttpClientErrorException exception) {

        return ExceptionInformation.builder()
                .error(exception.getStatusText())
                .status(exception.getStatusCode().value())
                .message(exception.getStatusText())
                .path(serverWebExchange.getRequest().getPath().value())
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