package no.nav.testnav.libs.reactivesessionsecurity.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.server.WebSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import no.nav.testnav.libs.reactivesessionsecurity.resolver.logut.OcidLogoutUriResolver;

public class LogoutSuccessHandler implements ServerLogoutSuccessHandler {

    private static final String DEFAULT_LOGOUT_STATE = "logout";
    private static final Set<String> VALID_LOGOUT_STATES = Set.of(
            DEFAULT_LOGOUT_STATE,
            "organisation_error",
            "unknown_error",
            "miljoe_error",
            "person_org_error",
            "azure_error",
            "session_error"
    );

    private final Map<String, OcidLogoutUriResolver> resolvers = new HashMap<>();

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        ServerHttpResponse response = exchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getCookies().remove("JSESSIONID");
        response.getCookies().remove("sessionExpiry");
        response.getCookies().remove("serverTime");

        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            var registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
            var logOutState = getLogoutState(exchange, registrationId);
            return Optional
                    .ofNullable(resolvers.get(registrationId))
                    .map(resolver -> resolver.generateUrl((DefaultOidcUser) authentication.getPrincipal(), logOutState))
                    .orElse(Mono.empty())
                    .switchIfEmpty(Mono.just(buildLoginUri(logOutState)))
                    .doOnNext(uri -> response.getHeaders().setLocation(uri))
                    .then(exchange
                            .getExchange()
                            .getSession()
                            .flatMap(WebSession::invalidate)
                    );
        }
        response.getHeaders().setLocation(buildLoginUri(DEFAULT_LOGOUT_STATE));
        return exchange
                .getExchange()
                .getSession()
                .flatMap(WebSession::invalidate);
    }

    private String getLogoutState(WebFilterExchange exchange, String registrationId) {
        var request = exchange.getExchange().getRequest();
        return normalizeLogoutState(request.getQueryParams().getFirst("state"), registrationId);
    }

    static String normalizeLogoutState(String state, String registrationId) {
        if (state == null || !VALID_LOGOUT_STATES.contains(state)) {
            return DEFAULT_LOGOUT_STATE;
        }
        if ("aad".equals(registrationId) && "organisation_error".equals(state)) {
            return "unknown_error";
        }
        return state;
    }

    static URI buildLoginUri(String logoutState) {
        return UriComponentsBuilder
                .fromPath("/login")
                .queryParam("state", logoutState)
                .build()
                .encode()
                .toUri();
    }

    public void applyOn(String authorizedClientRegistrationId, OcidLogoutUriResolver resolver) {
        resolvers.put(authorizedClientRegistrationId, resolver);
    }
}
