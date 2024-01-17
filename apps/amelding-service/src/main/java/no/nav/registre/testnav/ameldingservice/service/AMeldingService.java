package no.nav.registre.testnav.ameldingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnav.ameldingservice.config.Consumers;
import no.nav.registre.testnav.ameldingservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnav.ameldingservice.domain.AMelding;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AMeldingService {
    private final OppsummeringsdokumentConsumer oppsummeringsdokumentConsumer;
    private final Consumers consumers;
    private final TokenExchange tokenExchange;

    public Mono<String> save(AMelding aMelding, String miljo) {
        return tokenExchange.exchange(consumers.getOppsummeringsdokumentService()).flatMap(accessToken -> oppsummeringsdokumentConsumer
                .get(
                        aMelding.getOpplysningspliktigOrganisajonsnummer(),
                        aMelding.getKalendermaaned(),
                        miljo,
                        accessToken
                ).map(aMelding::updateOppsummeringsdokumentDTO)
                .switchIfEmpty(Mono.just(aMelding.newOppsummeringsdokumentDTO()))
                .flatMap(oppsummeringsdokument -> oppsummeringsdokumentConsumer.save(oppsummeringsdokument, miljo, accessToken)));
    }

    public Mono<AMelding> get(String id) {
        Mono<String> accessToken = tokenExchange.exchange(consumers.getOppsummeringsdokumentService()).map(AccessToken::getTokenValue);
        return oppsummeringsdokumentConsumer.get(id, accessToken).map(AMelding::new);
    }
}