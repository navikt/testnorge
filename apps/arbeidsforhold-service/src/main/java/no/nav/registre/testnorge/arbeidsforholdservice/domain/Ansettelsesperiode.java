package no.nav.registre.testnorge.arbeidsforholdservice.domain;

import lombok.AllArgsConstructor;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.AnsettelsesperiodeDTO;

@AllArgsConstructor
public class Ansettelsesperiode {

    private final AnsettelsesperiodeDTO dto;

    public no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.AnsettelsesperiodeDTO toDTO() {
        var builder = no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.AnsettelsesperiodeDTO.builder();

        return builder
                .periode(dto.getPeriode() == null ? null : new Periode(dto.getPeriode()).toDTO())
                .sluttaarsak(dto.getSluttaarsak())
                .build();
    }

}
