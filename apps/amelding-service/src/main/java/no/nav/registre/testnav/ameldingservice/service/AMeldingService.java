package no.nav.registre.testnav.ameldingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import no.nav.registre.testnav.ameldingservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnav.ameldingservice.domain.AMelding;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;

@Service
@RequiredArgsConstructor
public class AMeldingService {
    private final OppsummeringsdokumentConsumer oppsummeringsdokumentConsumer;

    public Mono<String> save(AMelding aMelding, String miljo) {
        return oppsummeringsdokumentConsumer
                .get(
                        aMelding.getOpplysningspliktigOrganisajonsnummer(),
                        aMelding.getKalendermaaned(),
                        miljo
                ).map(aMelding::updateOppsummeringsdokumentDTO)
                .switchIfEmpty(Mono.just(aMelding.newOppsummeringsdokumentDTO()))
                .flatMap(oppsummeringsdokument -> oppsummeringsdokumentConsumer.save(oppsummeringsdokument, miljo));
    }

    public Mono<AMelding> get(String id) {
        return oppsummeringsdokumentConsumer.get(id).map(AMelding::new);
    }
}
