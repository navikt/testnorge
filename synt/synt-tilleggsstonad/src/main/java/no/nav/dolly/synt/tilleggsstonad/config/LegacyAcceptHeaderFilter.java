package no.nav.dolly.synt.tilleggsstonad.config;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
class LegacyAcceptHeaderFilter implements WebFilter {

    private static final String API_PATH_PREFIX = "/api/";

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        var request = exchange.getRequest();
        if (!request.getPath().value().startsWith(API_PATH_PREFIX) || acceptsJson(request.getHeaders())) {
            return chain.filter(exchange);
        }
        return chain.filter(exchange
                .mutate()
                .request(request
                        .mutate()
                        .headers(headers -> headers.setAccept(List.of(MediaType.APPLICATION_JSON)))
                        .build())
                .build());

    }

    private static boolean acceptsJson(HttpHeaders headers) {

        try {
            var accept = headers.getAccept();
            return accept.isEmpty() || accept.stream().anyMatch(MediaType.APPLICATION_JSON::isCompatibleWith);
        } catch (InvalidMediaTypeException e) {
            return false;
        }

    }

}
