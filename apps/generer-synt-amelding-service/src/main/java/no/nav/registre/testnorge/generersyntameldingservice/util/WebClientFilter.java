package no.nav.registre.testnorge.generersyntameldingservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class WebClientFilter {

    public static boolean is5xxException(Throwable throwable) {

        return throwable instanceof WebClientResponseException wce &&
                wce.getStatusCode().is5xxServerError();
    }

    public static String getMessage(Throwable throwable) {
        return throwable instanceof WebClientResponseException webClientResponseException ?
                webClientResponseException.getResponseBodyAsString(StandardCharsets.UTF_8) :
                throwable.getMessage();
    }

    public static void logErrorMessage(Throwable throwable) {
        log.error(getMessage(throwable), throwable);
    }
}
