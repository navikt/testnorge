package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.testnav.libs.securitycore.domain.UserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.BANKID;
import static no.nav.dolly.util.TokenXUtil.getBankidUser;

@UtilityClass
@Slf4j
public final class CurrentAuthentication {

    public static Bruker getAuthUser(GetUserInfo userInfo) {
        JwtAuthenticationToken token = getToken();
        UserInfo bankidUser = getBankidUser(userInfo);
        return Bruker.builder()
                .brukerId(nonNull(bankidUser) ? bankidUser.id() : azureUserId())
                .brukernavn(nonNull(bankidUser)
                        ? bankidUser.brukernavn()
                        : Optional.ofNullable((String) token.getToken().getClaims().get("name")).orElse("Systembruker"))
                .epost((String) token.getToken().getClaims().get("preferred_username"))
                .brukertype(nonNull(bankidUser) ? BANKID : AZURE)
                .build();
    }

    public static String getUserId(GetUserInfo userInfo) {
        UserInfo bankidUser = getBankidUser(userInfo);
        return nonNull(bankidUser)
                ? bankidUser.id()
                : azureUserId();
    }

    public static String azureUserId() {
        return (String) getToken().getToken().getClaims().get("oid");
    }

    private static JwtAuthenticationToken getToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(o -> o instanceof JwtAuthenticationToken)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow(() -> new RuntimeException("Finner ikke Jwt Authentication Token"));
    }
}