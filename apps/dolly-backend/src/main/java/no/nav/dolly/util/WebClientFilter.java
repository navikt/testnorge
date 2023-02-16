package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class WebClientFilter {

    public static boolean is5xxException(Throwable throwable) {

        return (throwable instanceof WebClientResponseException wce &&
                wce.getStatusCode().is5xxServerError()) ||
                throwable.getMessage().contains("nested exception is java.net.SocketException");
    }

    public static String getMessage(Throwable throwable) {

        return throwable instanceof WebClientResponseException webClientResponseException?
                webClientResponseException.getResponseBodyAsString(StandardCharsets.UTF_8) :
                throwable.getMessage();
    }

    public static HttpStatus getStatus(Throwable throwable) {

        return throwable instanceof WebClientResponseException webClientResponseException?
                webClientResponseException.getStatusCode() :
                HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public static void logErrorMessage(Throwable throwable) {

        if (!(throwable instanceof WebClientResponseException)) {
            log.error(throwable.getMessage(), throwable);
        }
    }
}
