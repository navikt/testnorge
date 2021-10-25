package no.nav.testnav.libs.servletsecurity.action;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.servletsecurity.domain.Token;

@Component
@RequiredArgsConstructor
public class GetAuthenticatedToken extends JwtResolver implements Callable<Token> {

    @Override
    public Token call() {
        var jwt = getJwtAuthenticationToken();
        return new Token(jwt.getToken().getTokenValue(), null, false);
    }
}
