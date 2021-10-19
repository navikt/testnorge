package no.nav.registre.testnorge.profil.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

import no.nav.registre.testnorge.profil.consumer.AzureAdProfileConsumer;
import no.nav.registre.testnorge.profil.domain.Profil;
import no.nav.testnav.libs.servletsecurity.properties.TokenXResourceServerProperties;

@Service
@RequiredArgsConstructor
public class ProfilService {
    private final AzureAdProfileConsumer azureAdProfileConsumer;
    private final TokenXResourceServerProperties tokenXResourceServerProperties;

    private JwtAuthenticationToken getJwtAuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(o -> o instanceof JwtAuthenticationToken)
                .map(JwtAuthenticationToken.class::cast)
                .orElseThrow();
    }

    public Profil getProfile() {
        if (isTokenX()) {
            return new Profil("BankId", "[ukjent]", "[ukjent]", "[ukjent]");
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
