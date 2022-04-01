package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@UtilityClass
public class WebClientFilter {

    public static boolean is5xxException(Throwable throwable) {

        return throwable instanceof WebClientResponseException wce &&
                wce.getStatusCode().is5xxServerError();
    }

    public static String getMessage(Throwable throwable) {

        return throwable instanceof WebClientResponseException ?
                ((WebClientResponseException) throwable).getResponseBodyAsString() :
                throwable.getMessage();
    }
}
