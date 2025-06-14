package no.nav.testnav.identpool.providers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.testnav.identpool.exception.IdentAlleredeIBrukException;
import no.nav.testnav.identpool.exception.UgyldigDatoException;
import no.nav.testnav.identpool.exception.UgyldigPersonidentifikatorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
@NoArgsConstructor
public class HttpExceptionAdvice {

      private ResponseEntity<ExceptionInformation> informationForException(RuntimeException exception, ServerWebExchange serverWebExchange, HttpStatus status) {

        var exceptionInfo = ResponseEntity.status(status)
                .body(ExceptionInformation.builder()
                .error(status.getReasonPhrase())
                .status(status.value())
                .message(exception.getMessage())
                .path(serverWebExchange.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
                .timestamp(LocalDateTime.now())
                .build());

        if (status.is5xxServerError()) {
            log.error("Internal Server Error: {}", exceptionInfo, exception);
        } else if (status.is4xxClientError()) {
            log.warn("Client Error: {}", exceptionInfo);
        } else {
            log.info("Handled Exception: {}", exceptionInfo);
        }

        return exceptionInfo;
    }

    @ResponseBody
    @ExceptionHandler({IdentAlleredeIBrukException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    ResponseEntity<ExceptionInformation> internalServerError(ServerWebExchange serverWebExchange, RuntimeException exception) {
        return informationForException(exception, serverWebExchange, HttpStatus.CONFLICT);
    }

    @ResponseBody
    @ExceptionHandler({UgyldigDatoException.class, UgyldigPersonidentifikatorException.class, ForFaaLedigeIdenterException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    ResponseEntity<ExceptionInformation> badRequest(ServerWebExchange serverWebExchange, RuntimeException exception) {
        return informationForException(exception, serverWebExchange, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler({RuntimeException.class})
    ResponseEntity<ExceptionInformation> general(ServerWebExchange serverWebExchange, RuntimeException exception) {
        return informationForException(exception, serverWebExchange, resolveStatusCode(exception));
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

    private HttpStatus resolveStatusCode(RuntimeException exception) {

        return switch (exception) {
            case WebClientResponseException webClientResponseException ->
                    HttpStatus.valueOf(webClientResponseException.getStatusCode().value());
            case ResponseStatusException responseStatusException ->
                    HttpStatus.valueOf(responseStatusException.getStatusCode().value());
            case null, default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}