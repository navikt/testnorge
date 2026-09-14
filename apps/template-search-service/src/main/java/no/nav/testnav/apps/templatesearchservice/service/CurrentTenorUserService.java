package no.nav.testnav.apps.templatesearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalOwner;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class CurrentTenorUserService {

    private final GetAuthenticatedToken getAuthenticatedToken;
    private final GetUserInfo getUserInfo;
    private final TenorPersonMalValidationService validationService;

    public Mono<TenorMalOwner> getCurrentUser() {
        return getAuthenticatedToken.call()
                .filter(token -> !token.isClientCredentials())
                .flatMap(_ -> getUserInfo.call())
                .switchIfEmpty(Mono.error(new AccessDeniedException("Autentisert bruker mangler.")))
                .map(this::validateUserInfo)
                .map(this::toOwner);
    }

    private UserInfoExtended validateUserInfo(UserInfoExtended userInfo) {
        if (isBlank(userInfo.id()) ||
                isBlank(userInfo.brukernavn()) ||
                userInfo.id().length() > 100 ||
                userInfo.brukernavn().length() > 100) {
            throw new AccessDeniedException("Autentisert bruker mangler påkrevde claims.");
        }
        validationService.validateNoPersonidentifikator(userInfo.id());
        validationService.validateNoPersonidentifikator(userInfo.brukernavn());
        return userInfo;
    }

    private TenorMalOwner toOwner(UserInfoExtended userInfo) {
        return new TenorMalOwner(
                userInfo.id(),
                userInfo.brukernavn(),
                toBrukerType(userInfo));
    }

    private static TenorMalBrukerType toBrukerType(UserInfoExtended userInfo) {
        return userInfo.isBankId() ? TenorMalBrukerType.BANKID : TenorMalBrukerType.AZURE;
    }
}
