package no.nav.dolly.provider.advice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.KodeverkException;
import no.nav.dolly.exceptions.MissingHttpHeaderException;
import no.nav.dolly.exceptions.NotFoundException;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

import static no.nav.dolly.util.DateZoneUtil.CET;

@Slf4j
@ControllerAdvice
public class HttpExceptionAdvice extends DefaultErrorWebExceptionHandler {

    private static final String GATEWAY_ORIGINAL_REQUEST_URL = "org.springframework.web.reactive.HandlerMapping.pathWithinHandlerMapping";

    public HttpExceptionAdvice(ErrorAttributes errorAttributes,
                               WebProperties webProperties,
                               ErrorProperties errorProperties,
                               ApplicationContext applicationContext,
                               ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), errorProperties, applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    private ExceptionInformation informationForException(RuntimeException exception, ServerWebExchange serverWebExchange, HttpStatus status) {

        var exceptionInfo = ExceptionInformation.builder()
                .error(status.getReasonPhrase())
                .status(status.value())
                .message(exception.getMessage())
                .path(serverWebExchange.getAttribute(GATEWAY_ORIGINAL_REQUEST_URL))
                .timestamp(LocalDateTime.now(CET))
                .build();
        log.warn("HttpException: {}", exceptionInfo);
        return exceptionInfo;
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

    @ResponseBody
    @ExceptionHandler({ DollyFunctionalException.class, IllegalArgumentException.class, ConstraintViolationException.class,
            MissingHttpHeaderException.class, KodeverkException.class, WebClientResponseException.BadRequest.class })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    ExceptionInformation badRequest(ServerWebExchange serverWebExchange, RuntimeException exception) {
        return informationForException(exception, serverWebExchange, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler({ NotFoundException.class, WebClientResponseException.NotFound.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    ExceptionInformation notFoundRequest(ServerWebExchange serverWebExchange, RuntimeException exception) {
        return informationForException(exception, serverWebExchange, HttpStatus.NOT_FOUND);
    }
}