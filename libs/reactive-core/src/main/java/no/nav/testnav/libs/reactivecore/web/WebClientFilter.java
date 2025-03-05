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

@UtilityClass
@Slf4j
public class WebClientFilter {

    private static final String TIMEOUT = "Mottaker svarer ikke, eller har for lang svartid.";
    private static final String UNREACHABLE = "Forbindelsen er ustabil og mottaker kunne ikke nås.";

    public static boolean is5xxException(Throwable throwable) {
        return throwable instanceof WebClientResponseException responseException &&
                responseException.getStatusCode().is5xxServerError() ||
                throwable instanceof WebClientRequestException requestException &&
                        requestException.getCause() instanceof SocketException;
    }

    public static String getMessage(Throwable throwable) {
        switch (throwable) {

            case WebClientResponseException e -> {
                return e.getResponseBodyAsString(StandardCharsets.UTF_8);
            }

            case WebClientRequestException e -> {
                switch (e.getCause()) {
                    case ConnectTimeoutException ignored -> {
                        return TIMEOUT;
                    }
                    case ReadTimeoutException ignored -> {
                        return TIMEOUT;
                    }
                    case WriteTimeoutException ignored -> {
                        return TIMEOUT;
                    }
                    case SocketException ignored -> {
                        return UNREACHABLE;
                    }
                    default -> {
                        return e.getCause().toString();
                    }
                }
            }

            default -> {
                return throwable.getMessage();
            }

        }
    }

    public static HttpStatus getStatus(Throwable throwable) {
        return switch (throwable) {
            case WebClientResponseException e -> HttpStatus.valueOf(e.getStatusCode().value());
            case TimeoutException ignored -> HttpStatus.REQUEST_TIMEOUT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    public static void logErrorMessage(Throwable throwable) {
        if ((throwable instanceof WebClientResponseException e)) {
            log.error("CATO1: {} {}", throwable.getMessage(), e.getResponseBodyAsString(), throwable);
        } else {
            log.error("CATO2: {}", throwable.getMessage(), throwable);
        }
    }
}