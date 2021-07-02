package no.nav.registre.testnorge.arbeidsforholdservice.domain;

import lombok.AllArgsConstructor;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsavtaleDTO;

@AllArgsConstructor
public class Arbeidsavtale {

    private final ArbeidsavtaleDTO dto;


    public no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.ArbeidsavtaleDTO toDTO() {

        return no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.ArbeidsavtaleDTO
                .builder()
                .ansettelsesform(dto.getAnsettelsesform())
                .antallTimerPrUke(toDouble(dto.getAntallTimerPrUke()))
                .stillingsprosent(toDouble(dto.getStillingsprosent()))
                .arbeidstidsordning(dto.getArbeidstidsordning())
                .sistStillingsendring(dto.getSistStillingsendring())
                .build();
    }

    private Double toDouble(Float value){
        return value == null ? null : value.doubleValue();
    }

}
