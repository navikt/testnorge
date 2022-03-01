package no.nav.testnav.libs.servletsecurity.action;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
public class GetAuthenticatedToken extends JwtResolver implements Callable<Token> {

    private final GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;

    @Override
    public Token call() {
        var jwt = getJwtAuthenticationToken();
        return switch (getAuthenticatedResourceServerType.call()) {
            case AZURE_AD -> Token.builder()
                    .value(jwt.getToken().getTokenValue())
                    .userId(jwt.getTokenAttributes().get("oid").toString())
                    .clientCredentials(jwt.getTokenAttributes().get("oid").equals(jwt.getTokenAttributes().get("sub")))
                    .expiredAt(jwt.getToken().getExpiresAt())
                    .build();
            case TOKEN_X -> Token.builder()
                    .value(jwt.getToken().getTokenValue())
                    .userId(jwt.getTokenAttributes().get("pid").toString())
                    .clientCredentials(false)
                    .expiredAt(jwt.getToken().getExpiresAt())
                    .build();
        };
    }
}
