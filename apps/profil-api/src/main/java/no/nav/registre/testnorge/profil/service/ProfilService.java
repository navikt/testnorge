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
import reactor.core.publisher.Mono;

import java.util.Optional;

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

    public Mono<Profil> getProfile() {

        if (isTokenX()) {
            return getUserInfo.call()
                    .map(userInfo -> organisasjonTilgangConsumer
                            .getOrganisasjon(getIdent(), userInfo.organisasjonsnummer())
                            .map(organisasjon -> new Profil(
                                    userInfo.brukernavn(),
                                    UKJENT,
                                    UKJENT,
                                    organisasjon.getNavn(),
                                    userInfo.organisasjonsnummer(),
                                    BANK_ID)
                            ))
                    .orElse(Mono.just(new Profil(
                            BANK_ID,
                            UKJENT,
                            UKJENT,
                            UKJENT,
                            UKJENT,
                            BANK_ID
                    )));
        }
        return azureAdProfileConsumer.getProfil();
    }

    public Optional<byte[]> getImage() {
        return isTokenX() ? Optional.empty() : azureAdProfileConsumer.getProfilImage();
    }

    private Optional<JwtAuthenticationToken> getJwtAuthenticationToken() {

        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast);
    }

    private boolean isTokenX() {

        return getJwtAuthenticationToken()
                .map(token -> token
                        .getTokenAttributes()
                        .get(JwtClaimNames.ISS)
                        .equals(tokenXResourceServerProperties.getIssuerUri()))
                .orElseThrow();
    }

    private String getIdent() {

        return getJwtAuthenticationToken()
                .map(JwtAuthenticationToken::getTokenAttributes)
                .map(attribs -> attribs.get("pid"))
                .map(ident -> (String) ident)
                .orElseThrow();
    }
}
