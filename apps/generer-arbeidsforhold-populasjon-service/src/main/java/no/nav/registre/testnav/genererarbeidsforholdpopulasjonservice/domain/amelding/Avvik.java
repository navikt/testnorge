package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import lombok.Value;

import no.nav.testnav.libs.dto.syntrest.v1.AvvikDTO;

@Value
public class Avvik {
    String id;
    String navn;
    String alvorlighetsgrad;

    public Avvik(no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.AvvikDTO dto){
        id = dto.getId();
        navn = dto.getNavn();
        alvorlighetsgrad = dto.getAlvorlighetsgrad();
    }

    public Avvik(AvvikDTO dto) {
        id = dto.getId();
        navn = dto.getNavn();
        alvorlighetsgrad = dto.getAlvorlighetsgrad();
    }

    public no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.AvvikDTO toDTO(){
        return no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.AvvikDTO
                .builder()
                .navn(navn)
                .id(id)
                .alvorlighetsgrad(alvorlighetsgrad)
                .build();
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
