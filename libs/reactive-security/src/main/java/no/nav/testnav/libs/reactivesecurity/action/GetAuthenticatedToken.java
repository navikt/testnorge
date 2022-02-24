package no.nav.testnav.libs.reactivesecurity.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
                            .map(jwt -> {
                                log.info("jaajajaaa!");
                                return Token.builder()
                                        .clientCredentials(false)
                                        .userId(jwt.getTokenAttributes().get("pid").toString())
                                        .value(jwt.getToken().getTokenValue())
                                        .expiredAt(jwt.getToken().getExpiresAt())
                                        .build();
                            });
                    case AZURE_AD -> getJwtAuthenticationToken()
                            .map(jwt -> {
                                log.info("jaajaj!");
                                return Token.builder()
                                        .clientCredentials(jwt.getTokenAttributes().get("oid").equals(jwt.getTokenAttributes().get("sub")))
                                        .userId(jwt.getTokenAttributes().get("oid").toString())
                                        .value(jwt.getToken().getTokenValue())
                                        .expiredAt(jwt.getToken().getExpiresAt())
                                        .build();
                            });
                });
    }
}
