package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.domain.UserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import no.nav.testnav.libs.servletsecurity.properties.TokenXResourceServerProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static java.util.Objects.isNull;

@UtilityClass
@Slf4j
public final class TokenXUtil {

    public static String getUserJwt() {

        var requestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (isNull(requestAttributes)) {
            log.info("Klarte ikke å hente ut User-Jwt");
            return null;
        }
        return requestAttributes.getRequest().getHeader(UserConstant.USER_HEADER_JWT);
    }

    public static boolean isTokenX(TokenXResourceServerProperties serverProperties) {

        return getJwtAuthenticationToken()
                .getTokenAttributes()
                .get(JwtClaimNames.ISS)
                .equals(serverProperties.getIssuerUri());
    }

    public static UserInfo getBankidUser(GetUserInfo getUserInfo) {

        try {
            return getUserInfo.call()
                    .orElse(null);
        } catch (NullPointerException e) {
            log.info("Fant ikke BankID request");
            return null;
        }
    }

    private static JwtAuthenticationToken getJwtAuthenticationToken() {

        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow();
    }

}
