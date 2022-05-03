package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.UnknownHttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@UtilityClass
public class WebClientFilter {

    public static boolean is5xxException(Throwable throwable) {

        return throwable instanceof WebClientResponseException wce &&
                wce.getStatusCode().is5xxServerError();
    }

    public static boolean is555Exception(Throwable throwable) {

        return throwable instanceof UnknownHttpStatusCodeException wce &&
                wce.getStatusCode().is5xxServerError();
    }
}
