package no.nav.testnav.joarkdokumentservice.domain;

import lombok.Value;
import no.nav.testnav.joarkdokumentservice.consumer.dto.AvsenderMottakerDTO;

@Value
public class AvsenderMottaker {
    String type;
    String id;
    String navn;

    public AvsenderMottaker(AvsenderMottakerDTO dto) {
        type = dto.getType();
        id = dto.getId();
        navn = dto.getNavn();
    }

    public no.nav.testnav.joarkdokumentservice.controller.v2.dto.AvsenderMottakerDTO toDTO() {
        return no.nav.testnav.joarkdokumentservice.controller.v2.dto.AvsenderMottakerDTO
                .builder()
                .type(type)
                .id(id)
                .navn(navn)
                .build();
    }
}
