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

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    //   private final ReactorServerHttpRequest httpServletRequest;

    private ExceptionInformation getExceptionInformation(HttpStatus statusCode, String statusText) {
        return ExceptionInformation.builder()
                .error(statusCode.getReasonPhrase())
                .status(statusCode.value())
                .message(statusText)
                //          .path(httpServletRequest.getPath().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRequestException.class)
    public ExceptionInformation clientErrorException(InvalidRequestException exception) {
        return getExceptionInformation(exception.getStatusCode(), exception.getStatusText());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ExceptionInformation clientErrorException(NotFoundException exception) {
        return getExceptionInformation(exception.getStatusCode(), exception.getStatusText());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerException.class)
    public ExceptionInformation clientErrorException(InternalServerException exception) {
        return getExceptionInformation(exception.getStatusCode(), exception.getStatusText());
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