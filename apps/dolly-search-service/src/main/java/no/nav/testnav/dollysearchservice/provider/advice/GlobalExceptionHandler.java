package no.nav.testnav.dollysearchservice.provider.advice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    ExceptionInformation handleNullPointerException(ServerWebExchange serverWebExchange, NullPointerException exception) {
        String stackTrace = Arrays.stream(exception.getStackTrace())
                .filter(element -> element.getClassName().startsWith("no.nav.testnav"))
                .limit(15)
                .map(element -> String.format("%s.%s(%s:%d)", 
                        element.getClassName(), 
                        element.getMethodName(), 
                        element.getFileName(), 
                        element.getLineNumber()))
                .collect(Collectors.joining("\n  at "));
        
        log.error("NullPointerException i dolly-search-service: message={}, location=\n  at {}", 
                exception.getMessage(), stackTrace, exception);
        
        return ExceptionInformation.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Intern feil: " + exception.getMessage())
                .path(serverWebExchange.getRequest().getPath().value())
                .timestamp(LocalDateTime.now())
                .build();
    }
}

