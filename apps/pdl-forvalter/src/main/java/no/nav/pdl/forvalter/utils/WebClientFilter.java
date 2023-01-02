package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@UtilityClass
public class WebClientFilter {

    public static boolean is5xxException(Throwable throwable) {

        return throwable instanceof WebClientResponseException wce &&
                wce.getStatusCode().is5xxServerError();
    }

    public static void logError(Throwable throwable) {

        log.error(throwable instanceof WebClientResponseException wce ?
                wce.getResponseBodyAsString() : throwable.getMessage(), throwable);
    }
}
