package no.nav.registre.testnorge.libs.oauth2.service;

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
public class SecureAuthenticationTokenResolver implements AuthenticationTokenResolver {

    @Override
    public JwtAuthenticationToken jwtAuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(o -> o instanceof JwtAuthenticationToken)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow(() -> new RuntimeException("Finner ikke Jwt Authentication Token"));
    }

    @Override
    public boolean isClientCredentials() {
        var jwtAuthenticationToken = jwtAuthenticationToken();
        Map<String, Object> tokenAttributes = jwtAuthenticationToken.getTokenAttributes();
        return String.valueOf(tokenAttributes.get("oid")).equals(String.valueOf(tokenAttributes.get("sub")));
    }

    @Override
    public void verifyAuthentication() {
        var jwtAuthenticationToken = jwtAuthenticationToken();
        Jwt credentials = (Jwt) jwtAuthenticationToken.getCredentials();
        Instant expiresAt = credentials.getExpiresAt();
        if (expiresAt == null || expiresAt.isBefore(LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC))) {
            throw new CredentialsExpiredException("Jwt er utloept");
        }
    }
}
