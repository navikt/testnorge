package no.nav.dolly.bestilling.inntektsmelding.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inntektsmelding {

    private String miljoe;
    private String arbeidstakerFnr;
    private List<Inntektsmelding.InntektMelding> inntekter;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InntektMelding {

        private String virksomhetsnummer;
        private LocalDate dato;
        private Double beloep;
    }
}
