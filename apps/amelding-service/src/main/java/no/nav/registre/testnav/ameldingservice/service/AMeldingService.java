package no.nav.registre.testnav.ameldingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import no.nav.registre.testnav.ameldingservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnav.ameldingservice.domain.AMelding;

@Service
@RequiredArgsConstructor
public class AMeldingService {
    private final OppsummeringsdokumentConsumer oppsummeringsdokumentConsumer;

    public String save(AMelding aMelding, String miljo) {
        var oppsummeringsdokumentDTO = oppsummeringsdokumentConsumer.get(
                aMelding.getOpplysningspliktigOrganisajonsnummer(),
                aMelding.getKalendermaaned(),
                miljo
        );
        return oppsummeringsdokumentConsumer.saveOpplysningspliktig(
                oppsummeringsdokumentDTO
                        .map(aMelding::updateOppsummeringsdokumentDTO)
                        .orElse(aMelding.newOppsummeringsdokumentDTO())
                , miljo
        );
    }

    public Optional<AMelding> get(String id){
        var oppsummeringsdokumentDTO = oppsummeringsdokumentConsumer.get(id);
        return oppsummeringsdokumentDTO.map(AMelding::new);
    }

}
