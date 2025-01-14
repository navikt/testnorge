package no.nav.testnav.libs.servletsecurity.jwt;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

class NoopJwtDecoder implements JwtDecoder {

    @Override
    public Jwt decode(String token) throws JwtException {
        return Jwt.withTokenValue(null).build();
    }

}
