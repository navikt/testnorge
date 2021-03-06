package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import lombok.Value;

import no.nav.testnav.libs.dto.syntrest.v1.FartoeyDTO;

@Value
public class Fartoey {
    String skipsregister;
    String skipstype;
    String fartsomraade;

    public Fartoey(no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.FartoeyDTO dto) {
        skipsregister = dto.getSkipsregister();
        skipstype = dto.getSkipstype();
        fartsomraade = dto.getFartsomraade();
    }

    public Fartoey(FartoeyDTO dto) {
        skipsregister = dto.getSkipsregister();
        skipstype = dto.getSkipstype();
        fartsomraade = dto.getFartsomraade();
    }

    public no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.FartoeyDTO toDTO() {
        return no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.FartoeyDTO.builder()
                .fartsomraade(fartsomraade)
                .skipsregister(skipsregister)
                .skipstype(skipstype)
                .build();
    }

    public FartoeyDTO toSynt() {
        return FartoeyDTO
                .builder()
                .skipsregister(skipsregister)
                .skipstype(skipstype)
                .fartsomraade(fartsomraade)
                .build();
    }
}
