package no.nav.freg.token.provider.oidc;

import no.nav.freg.security.oidc.utility.openid.OpenIdConnectProvider;
import no.nav.freg.security.oidc.utility.openid.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtTokenProviderController {

    @Autowired
    private OpenIdConnectProvider provider;

    @GetMapping("/token/user")
    public Token getUserToken(@RequestHeader(value = "username") String username, @RequestHeader(value = "password") String password) {
        return provider.getUserToken(username, password);
    }
}
