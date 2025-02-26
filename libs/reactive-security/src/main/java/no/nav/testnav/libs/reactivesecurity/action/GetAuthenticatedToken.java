package no.nav.testnav.libs.reactivesecurity.action;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetAuthenticatedToken extends JwtResolver implements Callable<Mono<Token>> {

    private final GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;

    @Override
    public Mono<Token> call() {
        return getAuthenticatedResourceServerType
                .call()
                .flatMap(serverType -> switch (serverType) {
                    case TOKEN_X -> getJwtAuthenticationToken()
                            .map(JwtAuthenticationToken.class::cast)
                            .map(jwt -> Token.builder()
                                    .clientCredentials(false)
                                    .userId(jwt.getTokenAttributes().get("pid").toString())
                                    .accessTokenValue(jwt.getToken().getTokenValue())
                                    .expiresAt(jwt.getToken().getExpiresAt())
                                    .build());
                    case AZURE_AD -> getJwtAuthenticationToken()
                            .map(JwtAuthenticationToken.class::cast)
                            .map(jwt -> Token.builder()
                                    .clientCredentials(jwt.getTokenAttributes().get("oid").equals(jwt.getTokenAttributes().get("sub")))
                                    .userId(jwt.getTokenAttributes().get("oid").toString())
                                    .accessTokenValue(jwt.getToken().getTokenValue())
                                    .expiresAt(jwt.getToken().getExpiresAt())
                                    .build());
                });
    }

}
