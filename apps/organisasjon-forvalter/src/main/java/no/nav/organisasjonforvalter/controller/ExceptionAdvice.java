package no.nav.organisasjonforvalter.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UrlPathHelper;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

        private final HttpServletRequest httpServletRequest;
        private final UrlPathHelper urlPathHelper;

        @ResponseBody
        @ExceptionHandler(HttpClientErrorException.class)
        @ResponseStatus(value = HttpStatus.GONE)
        ExceptionInformation clientErrorException(HttpClientErrorException exception) {
            return ExceptionInformation.builder()
                    .error(exception.getStatusText())
                    .status(exception.getStatusCode().value())
                    .message(exception.getMessage())
                    .path(urlPathHelper.getPathWithinApplication(httpServletRequest))
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
