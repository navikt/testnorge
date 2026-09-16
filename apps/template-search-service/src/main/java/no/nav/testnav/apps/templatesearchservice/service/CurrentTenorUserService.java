package no.nav.testnav.apps.templatesearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.templatesearchservice.consumers.DollyBackendConsumer;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalOwner;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class CurrentTenorUserService {

    private static final Pattern TEAM_BRUKER_ID_PATTERN = Pattern.compile("team-bruker-id-\\d+");

    private final GetAuthenticatedToken getAuthenticatedToken;
    private final GetUserInfo getUserInfo;
    private final DollyBackendConsumer dollyBackendConsumer;
    private final TenorPersonMalValidationService validationService;

    public Mono<TenorMalOwner> getCurrentUser() {
        return getAuthenticatedToken.call()
                .filter(token -> !token.isClientCredentials())
                .flatMap(_ -> getUserInfo.call())
                .switchIfEmpty(Mono.error(new AccessDeniedException("Autentisert bruker mangler.")))
                .map(this::validateUserInfo)
                .flatMap(userInfo -> {
                    if (userInfo.isBankId()) {
                        return Mono.just(toOwner(userInfo));
                    }
                    return dollyBackendConsumer.getRepresentererTeamBrukerId()
                            .map(this::toTeamOwner)
                            .switchIfEmpty(Mono.fromSupplier(() -> toOwner(userInfo)));
                });
    }

    private UserInfoExtended validateUserInfo(UserInfoExtended userInfo) {
        validateOwner(userInfo.id(), userInfo.brukernavn());
        return userInfo;
    }

    private TenorMalOwner toOwner(UserInfoExtended userInfo) {
        return new TenorMalOwner(
                userInfo.id(),
                userInfo.brukernavn(),
                toBrukerType(userInfo));
    }

    private TenorMalOwner toTeamOwner(String teamBrukerId) {

        var trimmedTeamBrukerId = teamBrukerId.trim();
        if (!TEAM_BRUKER_ID_PATTERN.matcher(trimmedTeamBrukerId).matches()) {
            throw new AccessDeniedException("Dolly returnerte ugyldig teamkontekst.");
        }
        validateOwner(trimmedTeamBrukerId, trimmedTeamBrukerId);
        return new TenorMalOwner(
                trimmedTeamBrukerId,
                trimmedTeamBrukerId,
                TenorMalBrukerType.TEAM);
    }

    private void validateOwner(String brukerId, String brukernavn) {

        if (isBlank(brukerId) ||
                isBlank(brukernavn) ||
                brukerId.length() > 100 ||
                brukernavn.length() > 100) {
            throw new AccessDeniedException("Autentisert bruker mangler påkrevde claims.");
        }
        validationService.validateNoPersonidentifikator(brukerId);
        validationService.validateNoPersonidentifikator(brukernavn);
    }

    private static TenorMalBrukerType toBrukerType(UserInfoExtended userInfo) {
        return userInfo.isBankId() ? TenorMalBrukerType.BANKID : TenorMalBrukerType.AZURE;
    }
}
