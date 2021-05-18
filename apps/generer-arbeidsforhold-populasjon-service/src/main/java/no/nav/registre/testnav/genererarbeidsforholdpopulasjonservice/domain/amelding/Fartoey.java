package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.FartoeyDTO;

@Value
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Fartoey {
    String skipsregister;
    String skipstype;
    String fartsomraade;

    public Fartoey(no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.FartoeyDTO dto) {
        skipsregister = dto.getSkipsregister();
        skipstype = dto.getSkipstype();
        fartsomraade = dto.getFartsomraade();
    }

    public Fartoey(FartoeyDTO dto) {
        skipsregister = dto.getSkipsregister();
        skipstype = dto.getSkipstype();
        fartsomraade = dto.getFartsomraade();
    }

    public no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.FartoeyDTO toDTO() {
        return no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.FartoeyDTO.builder()
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
