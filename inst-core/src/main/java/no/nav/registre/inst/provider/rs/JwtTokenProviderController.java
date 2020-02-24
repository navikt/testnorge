package no.nav.registre.inst.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import no.nav.freg.security.oidc.utility.openid.OpenIdConnectProvider;
import no.nav.freg.security.oidc.utility.openid.Token;

@RestController
public class JwtTokenProviderController {

    @Autowired
    private OpenIdConnectProvider provider;

    @GetMapping("/token/user")
    public Token getUserToken(@RequestHeader(value = "username") String username, @RequestHeader(value = "password") String password) {
        return provider.getUserToken(username, password);
    }
}
