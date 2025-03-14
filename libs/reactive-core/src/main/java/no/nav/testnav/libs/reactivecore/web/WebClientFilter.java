package no.nav.testnav.libs.reactivecore.web;

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
@Deprecated
public class WebClientFilter {

    public static String getMessage(Throwable throwable) {
        switch (throwable) {
            case WebClientResponseException e -> {
                return e.getResponseBodyAsString(StandardCharsets.UTF_8);
            }
            case WebClientRequestException e -> {
                if (e.getCause() instanceof ConnectTimeoutException ||
                        e.getCause() instanceof ReadTimeoutException ||
                        e.getCause() instanceof WriteTimeoutException) {
                    return "Mottaker svarer ikke, eller har for lang svartid.";
                } else if (e.getCause() instanceof SocketException) {
                    return "Forbindelsen er ustabil og mottaker kunne ikke nås.";
                } else {
                    return e.getCause().toString();
                }
            }
            case TimeoutException ignored -> {
                return "Mottaker svarer ikke, eller har for lang svartid.";
            }
            default -> {
                return throwable.getMessage();
            }
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

}