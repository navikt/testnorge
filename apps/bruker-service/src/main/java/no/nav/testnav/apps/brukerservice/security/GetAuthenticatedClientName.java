package no.nav.testnav.apps.brukerservice.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GetAuthenticatedClientName {

    public Mono<String> call() {

        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .flatMap(authentication ->
                        Mono.justOrEmpty(authentication.getTokenAttributes().get("azp_name")))
                .map(Object::toString);
    }
}
