package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bruker;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

@UtilityClass
@Slf4j
public final class CurrentAuthentication {

    public static Bruker getAuthUser() {
        JwtAuthenticationToken token = getToken();
        return Bruker.builder()
                .brukerId(getUserId())
                .brukernavn(Optional.ofNullable((String) token.getToken().getClaims().get("name")).orElse("Systembruker"))
                .epost((String) token.getToken().getClaims().get("preferred_username"))
                .build();
    }

    public static String getJwtToken() {
        JwtAuthenticationToken jwtAuthenticationToken = getToken();
        Map<String, Object> tokenAttributes = jwtAuthenticationToken.getTokenAttributes();
        return jwtAuthenticationToken.getToken().getTokenValue();
    }

    public static String getUserId() {
        String userJwt = TokenXUtil.getUserJwt();
        return nonNull(userJwt)
                ? userJwt
                : (String) getToken().getToken().getClaims().get("oid");
    }

    private static JwtAuthenticationToken getToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(o -> o instanceof JwtAuthenticationToken)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow(() -> new RuntimeException("Finner ikke Jwt Authentication Token"));
    }
}