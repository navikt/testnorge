package no.nav.testnav.apps.tpsmessagingservice.provider;

import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.exception.BadRequestException;
import no.nav.testnav.apps.tpsmessagingservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UrlPathHelper;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final HttpServletRequest httpServletRequest;
    private final UrlPathHelper urlPathHelper;

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    ExceptionInformation clientErrorException(NotFoundException exception) {
        return ExceptionInformation.builder()
                .error(exception.getStatusText())
                .status(exception.getStatusCode().value())
                .message(exception.getMessage())
                .path(urlPathHelper.getPathWithinApplication(httpServletRequest))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    ExceptionInformation clientErrorException(BadRequestException exception) {
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