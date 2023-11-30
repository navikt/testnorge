package no.nav.organisasjonforvalter.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

@UtilityClass
@Slf4j
public final class CurrentAuthentication {

    public static String getUserId() {
        return (String) getToken().getToken().getClaims().get("oid");
    }

    private static JwtAuthenticationToken getToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow(() -> new RuntimeException("Finner ikke Jwt Authentication Token"));
    }
}