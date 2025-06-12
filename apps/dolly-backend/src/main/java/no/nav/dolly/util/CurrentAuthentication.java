package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.BANKID;
import static no.nav.dolly.util.TokenXUtil.getBankidUser;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
@Slf4j
public final class CurrentAuthentication {

    public static Mono<Bruker> getAuthUser(GetUserInfo userInfo) {

        return userInfo.call()
                .map(userInfoExtended ->
                        Bruker.builder()
                                .brukerId(userInfoExtended.id())
                                .brukernavn(isNotBlank(userInfoExtended.brukernavn()) ?
                                        userInfoExtended.brukernavn() :
                                        "Systembruker")
                                .epost(userInfoExtended.epost())
                                .brukertype(userInfoExtended.isBankId() ? BANKID : AZURE)
                                .build()
                );
    }
}