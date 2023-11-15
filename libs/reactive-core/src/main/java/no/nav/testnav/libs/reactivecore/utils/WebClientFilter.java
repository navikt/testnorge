package no.nav.testnav.libs.reactivecore.utils;
import io.netty.channel.ChannelException;
import io.netty.resolver.dns.DnsNameResolverException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class WebClientFilter {

    public static boolean is5xxException(Throwable throwable) {

        return (throwable instanceof WebClientResponseException wce &&
                wce.getStatusCode().is5xxServerError()) ||
                throwable instanceof WebClientRequestException ||
                throwable instanceof DnsNameResolverException ||
                throwable instanceof IOException ||
                throwable instanceof ChannelException;
    }

    public static String getMessage(Throwable throwable) {

        return throwable instanceof WebClientResponseException webClientResponseException ?
                webClientResponseException.getResponseBodyAsString(StandardCharsets.UTF_8) :
                throwable.getMessage();
    }

    public static HttpStatus getStatus(Throwable throwable) {

        return throwable instanceof WebClientResponseException webClientResponseException ?
                HttpStatus.valueOf(webClientResponseException.getStatusCode().value()) :
                HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public static void logErrorMessage(Throwable throwable) {

        if (!(throwable instanceof WebClientResponseException)) {
            log.error(throwable.getMessage(), throwable);
        }
    }
}