package no.nav.dolly.libs.command;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.security.TokenExchangeException;
import no.nav.testnav.libs.reactivecore.utils.Errors;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.function.Function;

@UtilityClass
@Slf4j
public class Command {

    private static final Duration TIMEOUT = Duration.ofMillis(1000);

    public static <V> V fetch(WebClient webClient, Mono<AccessToken> accessToken, Function<UriBuilder, URI> uri, Class<V> responseType) {
        try {
            var bearerToken = accessToken
                    .blockOptional(TIMEOUT)
                    .orElseThrow(() -> new TokenExchangeException("Received empty token"))
                    .getTokenValue();
            return webClient
                    .get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, Errors::handle)
                    .bodyToMono(responseType)
                    .retryWhen(Errors.are(WebClientFilter::is5xxException))
                    .block(TIMEOUT);

        } catch (RuntimeException e) {
            var resolved = uri.apply(new DefaultUriBuilderFactory().builder());
            log.error("Failed to fetch from URI {}", resolved, e);
            throw new CommandException(resolved, e);
        }
    }

}
