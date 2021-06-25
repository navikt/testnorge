package no.nav.pdl.forvalter.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.exception.InternalServerException;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final HttpServletRequest httpServletRequest;
    private final UrlPathHelper urlPathHelper;

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRequestException.class)
    public ExceptionInformation clientErrorException(InvalidRequestException exception) {
        return ExceptionInformation.builder()
                .error(exception.getStatusCode().getReasonPhrase())
                .status(exception.getStatusCode().value())
                .message(exception.getStatusText())
                .path(urlPathHelper.getPathWithinApplication(httpServletRequest))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ExceptionInformation clientErrorException(NotFoundException exception) {
        return ExceptionInformation.builder()
                .error(exception.getStatusCode().getReasonPhrase())
                .status(exception.getStatusCode().value())
                .message(exception.getStatusText())
                .path(urlPathHelper.getPathWithinApplication(httpServletRequest))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerException.class)
    public ExceptionInformation clientErrorException(InternalServerException exception) {
        return ExceptionInformation.builder()
                .error(exception.getStatusCode().getReasonPhrase())
                .status(exception.getStatusCode().value())
                .message(exception.getStatusText())
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