package no.nav.registre.testnorge.arbeidsforholdservice.domain.v2;

import lombok.AllArgsConstructor;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.UtenlandsoppholdDTO;

@AllArgsConstructor
public class Utenlandsopphold {

    private final UtenlandsoppholdDTO dto;

    public no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.UtenlandsoppholdDTO toDTO() {


        return no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.UtenlandsoppholdDTO.builder()
                .periode(dto.getPeriode() == null ? null : new Periode(dto.getPeriode()).toDTO())
                .rapporteringsperiode(dto.getRapporteringsperiode())
                .build();
    }

}
