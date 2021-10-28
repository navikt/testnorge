package no.nav.testnav.libs.servletsecurity.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.servletsecurity.domain.Token;

@Component
@RequiredArgsConstructor
public class GetAuthenticatedToken extends JwtResolver implements Callable<Token> {

    private final GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;

    @Override
    public Token call() {
        var jwt = getJwtAuthenticationToken();
        return switch (getAuthenticatedResourceServerType.call()) {
            case AZURE_AD -> new Token(
                    jwt.getToken().getTokenValue(),
                    jwt.getTokenAttributes().get("oid").toString(),
                    jwt.getTokenAttributes().get("oid").equals(jwt.getTokenAttributes().get("sub"))
            );
            case TOKEN_X -> new Token(
                    jwt.getToken().getTokenValue(),
                    jwt.getTokenAttributes().get("pid").toString(),
                    false
            );
        };
    }
}
