package no.nav.testnav.apps.statusfrontend.config;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
class ApiTilgangsregler {

    private static final String IDTYP_CLAIM = "idtyp";
    private static final String APP_TOKEN = "app";
    private static final String OBJECT_ID_CLAIM = "oid";
    private static final String SCOPE_CLAIM = "scp";

    private final List<ResourceServerProperties> resourceServerProperties;

    Mono<AuthorizationResult> azureBruker(Mono<Authentication> authentication, AuthorizationContext context) {
        return vurder(authentication, this::erAzureBruker);
    }

    Mono<AuthorizationResult> azureBrukerEllerTokenX(Mono<Authentication> authentication, AuthorizationContext context) {
        return vurder(authentication, jwt -> erAzureBruker(jwt) || erTokenX(jwt));
    }

    private Mono<AuthorizationResult> vurder(Mono<Authentication> authentication, Predicate<Jwt> regel) {
        return authentication
                .filter(Authentication::isAuthenticated)
                .ofType(JwtAuthenticationToken.class)
                .map(token -> (AuthorizationResult) new AuthorizationDecision(regel.test(token.getToken())))
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    private boolean erAzureBruker(Jwt jwt) {
        return tokenType(jwt)
                .filter(ResourceServerType.AZURE_AD::equals)
                .isPresent()
                && !APP_TOKEN.equals(jwt.getClaimAsString(IDTYP_CLAIM))
                && erDelegertBrukertoken(jwt);
    }

    private boolean erDelegertBrukertoken(Jwt jwt) {
        var objectId = jwt.getClaimAsString(OBJECT_ID_CLAIM);
        var subject = jwt.getSubject();
        var scope = jwt.getClaimAsString(SCOPE_CLAIM);
        return harVerdi(objectId)
                && harVerdi(subject)
                && harVerdi(scope)
                && !objectId.equals(subject);
    }

    private static boolean harVerdi(String verdi) {
        return nonNull(verdi) && !verdi.isBlank();
    }

    private boolean erTokenX(Jwt jwt) {
        return tokenType(jwt)
                .filter(ResourceServerType.TOKEN_X::equals)
                .isPresent();
    }

    private Optional<ResourceServerType> tokenType(Jwt jwt) {
        var issuer = jwt.getIssuer();
        if (isNull(issuer)) {
            return Optional.empty();
        }
        return resourceServerProperties
                .stream()
                .filter(properties -> nonNull(properties.getIssuerUri()))
                .filter(properties -> properties.getIssuerUri().equalsIgnoreCase(issuer.toString()))
                .map(ResourceServerProperties::getType)
                .findFirst();
    }
}
