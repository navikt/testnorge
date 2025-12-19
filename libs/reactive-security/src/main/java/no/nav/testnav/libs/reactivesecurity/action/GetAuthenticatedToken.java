package no.nav.testnav.libs.reactivesecurity.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.Callable;

@Component
@Slf4j
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
