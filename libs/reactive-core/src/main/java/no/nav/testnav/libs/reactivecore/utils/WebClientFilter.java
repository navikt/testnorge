package no.nav.testnav.libs.reactivecore.utils;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Slf4j
@UtilityClass
public class WebClientFilter {

    public static boolean is5xxException(Throwable throwable) {

        return throwable instanceof WebClientResponseException responseException &&
                responseException.getStatusCode().is5xxServerError() ||
                throwable instanceof WebClientRequestException requestException &&
                        requestException.getCause() instanceof SocketException;
    }

    public static String getMessage(Throwable throwable) {

        if (throwable instanceof WebClientResponseException responseException) {
            return responseException.getResponseBodyAsString(StandardCharsets.UTF_8);

        } else if (throwable instanceof WebClientRequestException requestException) {

            if (requestException.getCause() instanceof ConnectTimeoutException ||
                    requestException.getCause() instanceof ReadTimeoutException ||
                    requestException.getCause() instanceof WriteTimeoutException) {

                return "Mottaker svarer ikke, eller har for lang svartid.";

            } else if (requestException.getCause() instanceof SocketException) {

                return "Forbindelsen er ustabil og mottaker kunne ikke nås.";

            } else {

                return requestException.getCause().toString();
            }

        } else if (throwable instanceof TimeoutException) {

            return "Mottaker svarer ikke, eller har for lang svartid.";

        } else {
            return throwable.getMessage();
        }
    }

    public static HttpStatus getStatus(Throwable throwable) {

        if (throwable instanceof WebClientResponseException webClientResponseException) {
            return HttpStatus.valueOf(webClientResponseException.getStatusCode().value());

        } else if (throwable instanceof TimeoutException) {
            return HttpStatus.REQUEST_TIMEOUT;

        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public static void logErrorMessage(Throwable throwable) {

        if ((throwable instanceof WebClientResponseException webClientResponseException)) {

            if (webClientResponseException.getStatusCode().is5xxServerError()) {
                    log.error("%s, %s".formatted(throwable.getMessage(),
                            webClientResponseException.getResponseBodyAsString()), throwable);
            } else {
                log.warn("%s, %s".formatted(throwable.getMessage(),
                        webClientResponseException.getResponseBodyAsString()));
            }
        } else {
            log.error(throwable.getMessage(), throwable);
        }
    }
}