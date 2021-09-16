package no.nav.testnav.libs.reactivesessionsecurity.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import no.nav.testnav.libs.reactivesessionsecurity.resolver.logut.OcidLogoutUriResolver;

public class LogoutSuccessHandler implements ServerLogoutSuccessHandler {

    private final Map<String, OcidLogoutUriResolver> resolvers = new HashMap<>();

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        ServerHttpResponse response = exchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getCookies().remove("JSESSIONID");
        response.getCookies().remove("sessionExpiry");
        response.getCookies().remove("serverTime");

        if (authentication instanceof OAuth2AuthenticationToken) {
            var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            return Optional
                    .ofNullable(resolvers.get(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()))
                    .map(resolver -> resolver.generateUrl((DefaultOidcUser) authentication.getPrincipal()))
                    .orElse(Mono.empty())
                    .switchIfEmpty(Mono.just(URI.create("/login?logout")))
                    .doOnNext(uri -> response.getHeaders().setLocation(uri))
                    .then(exchange
                            .getExchange()
                            .getSession()
                            .flatMap(WebSession::invalidate)
                    );
        }
        response.getHeaders().setLocation(URI.create("/login?logout"));
        return exchange
                .getExchange()
                .getSession()
                .flatMap(WebSession::invalidate);
    }

    public void applyOn(String authorizedClientRegistrationId, OcidLogoutUriResolver resolver) {
        resolvers.put(authorizedClientRegistrationId, resolver);
    }
}
