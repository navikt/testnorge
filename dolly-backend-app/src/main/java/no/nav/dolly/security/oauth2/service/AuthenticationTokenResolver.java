package no.nav.dolly.security.oauth2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class AuthenticationTokenResolver {


    private JwtAuthenticationToken jwtAuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(o -> o instanceof JwtAuthenticationToken)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow(() -> new RuntimeException("Finner ikke Authentication Token"));
    }

    public String getToken() {
        JwtAuthenticationToken jwtAuthenticationToken = jwtAuthenticationToken();
        Map<String, Object> tokenAttributes = jwtAuthenticationToken.getTokenAttributes();
        log.info("Hentet innlogget token for OID {}", tokenAttributes.get("oid"));
        return jwtAuthenticationToken.getToken().getTokenValue();
    }
}
