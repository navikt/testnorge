package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import lombok.Value;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.FartoeyDTO;

@Value
public class Fartoey {
    String skipsregister;
    String skipstype;
    String fartsomraade;

    public Fartoey(FartoeyDTO dto){
        skipsregister = dto.getSkipsregister();
        skipstype = dto.getSkipstype();
        fartsomraade = dto.getFartsomraade();
    }

    public FartoeyDTO toSynt(){
        return FartoeyDTO
                .builder()
                .skipsregister(skipsregister)
                .skipstype(skipstype)
                .fartsomraade(fartsomraade)
                .build();
    }
}
