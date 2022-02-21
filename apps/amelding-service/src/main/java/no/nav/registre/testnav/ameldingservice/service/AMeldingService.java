package no.nav.registre.testnav.ameldingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnav.ameldingservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnav.ameldingservice.credentials.OppsummeringsdokumentServerProperties;
import no.nav.registre.testnav.ameldingservice.domain.AMelding;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class AMeldingService {
    private final OppsummeringsdokumentConsumer oppsummeringsdokumentConsumer;
    private final OppsummeringsdokumentServerProperties applicationProperties;
    private final TokenExchange tokenExchange;

    public Mono<String> save(AMelding aMelding, String miljo, Mono<String> accessToken) {
        return oppsummeringsdokumentConsumer
                .get(
                        aMelding.getOpplysningspliktigOrganisajonsnummer(),
                        aMelding.getKalendermaaned(),
                        miljo,
                        nonNull(accessToken) ? accessToken : tokenExchange.exchange(applicationProperties).map(AccessToken::getTokenValue),
                        ).map(aMelding::updateOppsummeringsdokumentDTO)
                .switchIfEmpty(Mono.just(aMelding.newOppsummeringsdokumentDTO()))
                .flatMap(oppsummeringsdokument -> oppsummeringsdokumentConsumer.save(oppsummeringsdokument, miljo, accessToken));
    }

    public Flux<String> saveAll(List<AMelding> aMeldinger, String miljo) {
        Mono<String> accessToken = tokenExchange.exchange(applicationProperties).map(AccessToken::getTokenValue);
        return aMeldinger.stream().map(amelding -> save(amelding, miljo).flux());
    }

    public Mono<AMelding> get(String id) {
        Mono<String> accessToken = tokenExchange.exchange(applicationProperties).map(AccessToken::getTokenValue);
        return oppsummeringsdokumentConsumer.get(id, accessToken).map(AMelding::new);
    }
}
