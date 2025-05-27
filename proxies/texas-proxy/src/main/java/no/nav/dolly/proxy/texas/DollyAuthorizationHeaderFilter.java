package no.nav.dolly.proxy.texas;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
class DollyAuthorizationHeaderFilter implements WebFilter {

    private static final String PREFIX = "Dolly ";

    private final String sharedSecret;
    private final String[] whitelist;
    private final AntPathMatcher ant = new AntPathMatcher();

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        var currentPath = exchange
                .getRequest()
                .getPath()
                .value();
        var whitelisted = Arrays
                .stream(whitelist)
                .anyMatch(pattern -> ant.match(pattern, currentPath));
        if (whitelisted) {
            return chain.filter(exchange);
        }

        var header = exchange
                .getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null || header.isEmpty()) {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"missing_token\", error_description=\"You need a special Dolly token denoted by 'Dolly ' (not 'Bearer ').\"");
            return response.setComplete();
        }

        if (!header.startsWith(PREFIX)) {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\", error_description=\"This is not a Dolly token denoted by 'Dolly ' (not 'Bearer ').\"");
            return response.setComplete();
        }

        var providedSecret = header.substring(PREFIX.length());

        if (!sharedSecret.equals(providedSecret)) {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\", error_description=\"Invalid Dolly token\"");
            return response.setComplete();
        }

        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOLLY_SERVICE"));
        var authentication = new UsernamePasswordAuthenticationToken(providedSecret, null, authorities);
        return chain
                .filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

    }
}