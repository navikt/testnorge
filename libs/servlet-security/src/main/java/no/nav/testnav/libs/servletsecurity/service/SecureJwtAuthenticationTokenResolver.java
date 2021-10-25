package no.nav.testnav.libs.servletsecurity.service;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

@Component
@Deprecated
public class SecureJwtAuthenticationTokenResolver implements AuthenticationTokenResolver {

    private JwtAuthenticationToken getJwtAuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(o -> o instanceof JwtAuthenticationToken)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow(() -> new RuntimeException("Finner ikke Jwt Authentication Token"));
    }


    @Override
    public String getTokenValue() {
        return getJwtAuthenticationToken().getToken().getTokenValue();
    }

    @Override
    public boolean isClientCredentials() {
        Map<String, Object> tokenAttributes = getJwtAuthenticationToken().getTokenAttributes();
        return String.valueOf(tokenAttributes.get("oid")).equals(String.valueOf(tokenAttributes.get("sub")));
    }

    @Override
    public String getOid() {
        Map<String, Object> tokenAttributes = getJwtAuthenticationToken().getTokenAttributes();
        return tokenAttributes.get("oid") == null ? null : String.valueOf(tokenAttributes.get("oid"));
    }

    @Override
    public void verifyAuthentication() {
        Jwt credentials = (Jwt) getJwtAuthenticationToken().getCredentials();
        Instant expiresAt = credentials.getExpiresAt();
        if (expiresAt == null || expiresAt.isBefore(LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC))) {
            throw new CredentialsExpiredException("Jwt er utloept");
        }
    }
}
