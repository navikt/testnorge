package no.nav.dolly.util;

import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import lombok.experimental.UtilityClass;
import no.nav.dolly.domain.jpa.Bruker;

@UtilityClass
public final class CurrentAuthentication {

    private static JwtAuthenticationToken getToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(o -> o instanceof JwtAuthenticationToken)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow(() -> new RuntimeException("Finner ikke Jwt Authentication Token"));
    }

    public static Bruker getAuthUser() {
        JwtAuthenticationToken token = getToken();
        return Bruker.builder()
                .brukerId((String) token.getToken().getClaims().get("oid"))
                .brukernavn(Optional.ofNullable((String) token.getToken().getClaims().get("name")).orElse("Systembruker"))
                .epost((String) token.getToken().getClaims().get("preferred_username"))
                .build();
    }

    public static String getUserId() {
        return (String) getToken().getToken().getClaims().get("oid");
    }
}