package no.nav.registre.testnorge.profil.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.profil.consumer.AzureAdProfileConsumer;
import no.nav.registre.testnorge.profil.consumer.PersonOrganisasjonTilgangConsumer;
import no.nav.registre.testnorge.profil.domain.Profil;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import no.nav.testnav.libs.servletsecurity.properties.TokenXResourceServerProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfilService {
    private static final String UKJENT = "[ukjent]";
    private static final String BANK_ID = "BankId";
    private final AzureAdProfileConsumer azureAdProfileConsumer;
    private final TokenXResourceServerProperties tokenXResourceServerProperties;
    private final PersonOrganisasjonTilgangConsumer organisasjonTilgangConsumer;
    private final GetUserInfo getUserInfo;

    public Profil getProfile() {
        if (isTokenX()) {
            return getUserInfo.call()
                    .map(userInfo ->
                    organisasjonTilgangConsumer
                            .getOrganisasjon(userInfo.organisasjonsnummer())
                            .map(dto -> new Profil(
                                    userInfo.brukernavn(),
                                    UKJENT,
                                    UKJENT,
                                    dto.getNavn(),
                                    dto.getOrganisasjonsnummer(),
                                    BANK_ID)
                            ).block()
            ).orElse(new Profil(
                    BANK_ID,
                    UKJENT,
                    UKJENT,
                    UKJENT,
                    UKJENT,
                    BANK_ID
            ));
        }
        return azureAdProfileConsumer.getProfil();
    }

    public Optional<byte[]> getImage() {
        return isTokenX() ? Optional.empty() : azureAdProfileConsumer.getProfilImage();
    }

    private JwtAuthenticationToken getJwtAuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow();
    }

    private boolean isTokenX() {
        return getJwtAuthenticationToken()
                .getTokenAttributes()
                .get(JwtClaimNames.ISS)
                .equals(tokenXResourceServerProperties.getIssuerUri());
    }

}
