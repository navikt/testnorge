package no.nav.organisasjonforvalter.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

@UtilityClass
@Slf4j
public final class CurrentAuthentication {

    public static Mono<String> getUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(token -> (String) token.getToken().getClaims().get("oid"))
                .switchIfEmpty(Mono.error(new RuntimeException("Finner ikke Jwt Authentication Token")));
    }
}