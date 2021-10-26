package no.nav.freg.token.provider.oidc;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import no.nav.freg.token.provider.utility.openid.OpenIdConnectProvider;
import no.nav.freg.token.provider.utility.openid.Token;

@RestController
@RequiredArgsConstructor
public class JwtTokenProviderController {

    private final OpenIdConnectProvider provider;

    @GetMapping("/token/user")
    public Token getUserToken(
            @RequestHeader(value = "username") String username,
            @RequestHeader(value = "password") String password
    ) {
        return provider.getUserToken(username, password);
    }
}
