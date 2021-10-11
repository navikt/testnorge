package no.nav.testnav.libs.reactivesessionsecurity.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

import no.nav.testnav.libs.reactivesessionsecurity.config.TokenXConstants;

@Slf4j
@Configuration
public class TokenXSessionFilter implements WebFilter {

    private Mono<String> getClientId() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(OAuth2AuthenticationToken.class::cast)
                .map(token -> Optional.ofNullable(token).map(OAuth2AuthenticationToken::getAuthorizedClientRegistrationId))
                .flatMap(value -> value.map(Mono::just).orElseGet(Mono::empty));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return getClientId()
                .flatMap(client -> exchange.getSession())
                .map(session -> Optional.ofNullable(session.getAttribute(TokenXConstants.TOKENX_PERSON_REPRESENTING_KEY)))
                .flatMap(value -> value.map(Mono::just).orElseGet(Mono::empty))
                .map(representing -> exchange
                        .mutate()
                        .request(exchange
                                .getRequest()
                                .mutate()
                                .header(TokenXConstants.TOKENX_PERSON_REPRESENTING_HEADER, (String) representing)
                                .build()
                        ).build())
                .then()
                .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)));
    }
}
