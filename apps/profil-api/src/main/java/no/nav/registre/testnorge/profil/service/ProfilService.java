package no.nav.registre.testnorge.profil.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

import no.nav.registre.testnorge.profil.consumer.AzureAdProfileConsumer;
import no.nav.registre.testnorge.profil.consumer.PersonOrganisasjonTilgangConsumer;
import no.nav.registre.testnorge.profil.domain.Profil;
import no.nav.testnav.libs.securitycore.UserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserJwt;
import no.nav.testnav.libs.servletsecurity.properties.TokenXResourceServerProperties;

@Service
@RequiredArgsConstructor
public class ProfilService {
    private final AzureAdProfileConsumer azureAdProfileConsumer;
    private final TokenXResourceServerProperties tokenXResourceServerProperties;
    private final PersonOrganisasjonTilgangConsumer organisasjonTilgangConsumer;
    private final GetUserJwt getUserJwt;
    private final GetUserInfo getUserInfo;

    private JwtAuthenticationToken getJwtAuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(o -> o instanceof JwtAuthenticationToken)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow();
    }

    public Profil getProfile() {
        if (isTokenX()) {

            return getUserInfo.call().map(userInfo ->
                organisasjonTilgangConsumer
                        .getOrganisasjon(userInfo.organisasjonsnummer())
                        .map(dto -> new Profil(
                                userInfo.brukernavn(),
                                "[ukjent]",
                                "[ukjent]",
                                dto.organisasjonsnummer())
                        ).block()
            ).orElse(new Profil(
                    "BankId",
                    "[ukjent]",
                    "[ukjent]",
                    "[ukjent]"
            ));
        }
        return azureAdProfileConsumer.getProfil();
    }

    public Optional<byte[]> getImage() {
        return isTokenX() ? Optional.empty() : azureAdProfileConsumer.getProfilImage();
    }

    private boolean isTokenX() {
        return getJwtAuthenticationToken()
                .getTokenAttributes()
                .get(JwtClaimNames.ISS)
                .equals(tokenXResourceServerProperties.getIssuerUri());
    }

}
