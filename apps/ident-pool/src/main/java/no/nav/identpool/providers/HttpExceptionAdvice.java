package no.nav.identpool.providers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.identpool.exception.IdentAlleredeIBrukException;
import no.nav.identpool.exception.UgyldigDatoException;
import no.nav.identpool.exception.UgyldigPersonidentifikatorException;
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
public class HttpExceptionAdvice {

    private final HttpServletRequest httpServletRequest;
    private final UrlPathHelper urlPathHelper;

    @ResponseBody
    @ExceptionHandler({ IdentAlleredeIBrukException.class })
    @ResponseStatus(value = HttpStatus.CONFLICT)
    ExceptionInformation internalServerError(RuntimeException exception) {
        return informationForException(exception, HttpStatus.CONFLICT);
    }

    @ResponseBody
    @ExceptionHandler({ UgyldigDatoException.class, UgyldigPersonidentifikatorException.class, ForFaaLedigeIdenterException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    ExceptionInformation badRequest(RuntimeException exception) {
        return informationForException(exception, HttpStatus.BAD_REQUEST);
    }

    private ExceptionInformation informationForException(RuntimeException exception, HttpStatus status) {
        return ExceptionInformation.builder()
                .error(status.getReasonPhrase())
                .status(status.value())
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