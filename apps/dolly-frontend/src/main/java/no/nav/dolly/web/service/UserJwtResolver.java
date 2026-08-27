package no.nav.dolly.web.service;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.TestnavBrukerServiceProperties;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.securitycore.config.UserSessionConstant;
import no.nav.testnav.libs.securitycore.validation.IdentValidCheck;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class UserJwtResolver {

    private final GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final UserJwtExchange userJwtExchange;
    private final AccessService accessService;
    private final TestnavBrukerServiceProperties brukerServiceProperties;

    public UserJwtResolver(
            GetAuthenticatedResourceServerType getAuthenticatedResourceServerType,
            GetAuthenticatedUserId getAuthenticatedUserId,
            UserJwtExchange userJwtExchange,
            AccessService accessService,
            TestnavBrukerServiceProperties brukerServiceProperties) {
        this.getAuthenticatedResourceServerType = getAuthenticatedResourceServerType;
        this.getAuthenticatedUserId = getAuthenticatedUserId;
        this.userJwtExchange = userJwtExchange;
        this.accessService = accessService;
        this.brukerServiceProperties = brukerServiceProperties;
    }

    public Mono<String> resolve(ServerWebExchange exchange) {
        return getAuthenticatedResourceServerType.call()
                .switchIfEmpty(Mono.error(new AccessDeniedException("Autentiseringstype mangler for User-Jwt.")))
                .flatMap(resourceServerType -> switch (resourceServerType) {
                    case AZURE_AD -> resolveAzureUserJwt(exchange);
                    case TOKEN_X -> resolveIdportenUserJwt(exchange);
                });
    }

    private Mono<String> resolveAzureUserJwt(ServerWebExchange exchange) {
        return Mono.zip(
                        getAuthenticatedUserId.call()
                                .switchIfEmpty(Mono.error(new AccessDeniedException("Azure bruker-ID mangler."))),
                        accessService.getAccessToken(brukerServiceProperties, exchange)
                                .switchIfEmpty(Mono.error(new AccessDeniedException("Azure OBO-token mangler."))))
                .flatMap(values -> userJwtExchange.generateJwt(values.getT1(), values.getT2()))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Azure User-Jwt mangler.")));
    }

    private Mono<String> resolveIdportenUserJwt(ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> Mono.justOrEmpty(session.<String>getAttribute(UserSessionConstant.SESSION_USER_ID_KEY)))
                .flatMap(id -> {
                    if (!IdentValidCheck.isIdentValid(Set.of(id)).isEmpty()) {
                        return Mono.error(new AccessDeniedException("ID-porten User-Jwt skal ikke inneholde norsk personident."));
                    }
                    return userJwtExchange.generateJwt(id, exchange);
                });
    }
}
