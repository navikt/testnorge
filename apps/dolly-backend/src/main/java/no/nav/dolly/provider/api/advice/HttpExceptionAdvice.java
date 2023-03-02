package no.nav.dolly.provider.api.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.KodeverkException;
import no.nav.dolly.exceptions.MissingHttpHeaderException;
import no.nav.dolly.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UrlPathHelper;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class HttpExceptionAdvice {

    private final HttpServletRequest httpServletRequest;
    private final UrlPathHelper urlPathHelper;

    private ExceptionInformation informationForException(RuntimeException exception, HttpStatus status) {
        return ExceptionInformation.builder()
                .error(status.getReasonPhrase())
                .status(status.value())
                .message(exception.getMessage())
                .path(urlPathHelper.getPathWithinApplication(httpServletRequest))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseBody
    @ExceptionHandler({ DollyFunctionalException.class, ConstraintViolationException.class,
            MissingHttpHeaderException.class, KodeverkException.class, WebClientResponseException.BadRequest.class })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    ExceptionInformation badRequest(RuntimeException exception) {
        return informationForException(exception, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler({ NotFoundException.class, WebClientResponseException.NotFound.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    ExceptionInformation notFoundRequest(RuntimeException exception) {
        return informationForException(exception, HttpStatus.NOT_FOUND);
    }
}
