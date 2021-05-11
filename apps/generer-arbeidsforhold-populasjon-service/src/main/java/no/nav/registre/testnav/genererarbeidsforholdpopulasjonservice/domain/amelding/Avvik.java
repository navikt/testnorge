package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import lombok.Value;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.AvvikDTO;

@Value
public class Avvik {
    String id;
    String navn;
    String alvorlighetsgrad;

    public Avvik(AvvikDTO dto) {
        id = dto.getId();
        navn = dto.getNavn();
        alvorlighetsgrad = dto.getAlvorlighetsgrad();
    }

    public AvvikDTO toSynt(){
        return AvvikDTO
                .builder()
                .id(id)
                .navn(navn)
                .alvorlighetsgrad(alvorlighetsgrad)
                .build();
    }

}
