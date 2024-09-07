package no.nav.testnav.libs.commands.utils;

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

    public static void logErrorMessage(Throwable throwable) {

        if (!(throwable instanceof WebClientResponseException)) {
            log.error(throwable.getMessage(), throwable);
        }
    }
}