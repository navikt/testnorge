package no.nav.registre.testnorge.profil.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.profil.consumer.AzureAdProfileConsumer;
import no.nav.registre.testnorge.profil.consumer.PersonOrganisasjonTilgangConsumer;
import no.nav.registre.testnorge.profil.domain.Profil;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.reactivesecurity.properties.TokenxResourceServerProperties;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilService {
    private static final String UKJENT = "[ukjent]";
    private static final String BANK_ID = "BankId";
    private final AzureAdProfileConsumer azureAdProfileConsumer;
    private final TokenxResourceServerProperties tokenXResourceServerProperties;
    private final PersonOrganisasjonTilgangConsumer organisasjonTilgangConsumer;
    private final GetUserInfo getUserInfo;

    public Mono<Profil> getProfile() {
        return isTokenX()
                .flatMap(tokenX -> {
                    if (tokenX) {
                        return getUserInfo.call()
                                .flatMap(userInfo -> getIdent()
                                        .flatMap(ident -> organisasjonTilgangConsumer
                                                .getOrganisasjon(ident, userInfo.organisasjonsnummer())
                                                .map(organisasjon -> new Profil(
                                                        userInfo.brukernavn(),
                                                        UKJENT,
                                                        UKJENT,
                                                        organisasjon.getNavn(),
                                                        userInfo.organisasjonsnummer(),
                                                        BANK_ID))
                                        ))
                                .switchIfEmpty(Mono.just(new Profil(
                                        BANK_ID,
                                        UKJENT,
                                        UKJENT,
                                        UKJENT,
                                        UKJENT,
                                        BANK_ID
                                )));
                    }
                    return azureAdProfileConsumer.getProfil();
                });
    }

    public Mono<byte[]> getImage() {
        return isTokenX()
                .flatMap(tokenX -> {
                    if (tokenX) {
                        return Mono.empty();
                    }
                    return azureAdProfileConsumer.getProfilImage();
                });
    }

    private Mono<JwtAuthenticationToken> getJwtAuthenticationToken() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast);
    }

    private Mono<Boolean> isTokenX() {
        return getJwtAuthenticationToken()
                .map(token -> token
                        .getTokenAttributes()
                        .get(JwtClaimNames.ISS)
                        .equals(tokenXResourceServerProperties.getIssuerUri()));
    }

    private Mono<String> getIdent() {
        return getJwtAuthenticationToken()
                .map(JwtAuthenticationToken::getTokenAttributes)
                .map(attribs -> (String) attribs.get("pid"));
    }
}
