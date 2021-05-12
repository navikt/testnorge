package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.AvvikDTO;

@Value
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Avvik {
    String id;
    String navn;
    String alvorlighetsgrad;

    public Avvik(no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.AvvikDTO dto){
        id = dto.getId();
        navn = dto.getNavn();
        alvorlighetsgrad = dto.getAlvorlighetsgrad();
    }

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
