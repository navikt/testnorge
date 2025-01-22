package no.nav.registre.testnorge.profil.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

@Slf4j
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
            return getTokenXAttributes();
        }
        return azureAdProfileConsumer.getProfil();
    }

    public Optional<byte[]> getImage() {
        return isTokenX() ? Optional.empty() : azureAdProfileConsumer.getProfilImage();
    }

    private Optional<JwtAuthenticationToken> getJwtAuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(t -> {
                    log.info("JwtAuthenticationToken, attributes: {}",
                            t.getTokenAttributes().entrySet().stream()
                                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                                    .collect(Collectors.joining(",")));
                    return t;
                });
    }

    private boolean isTokenX() {

        return getJwtAuthenticationToken()
                .map(token -> token
                        .getTokenAttributes()
                        .get(JwtClaimNames.ISS)
                        .equals(tokenXResourceServerProperties.getIssuerUri()))
                .orElseThrow();
    }

    private Profil getTokenXAttributes() {

        return getJwtAuthenticationToken()
                .map(JwtAuthenticationToken::getTokenAttributes)
                .map(attribs -> new Profil(
                        (String) attribs.get("brukernavn"),
                        UKJENT,
                        UKJENT,
                        UKJENT,
                        (String) attribs.get("org"),
                        BANK_ID))
                .orElseThrow();
    }
}
