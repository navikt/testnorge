package no.nav.dolly.bestilling.inntektstub.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inntekt {

    public enum InntektType {LOENNSINNTEKT, YTELSE_FRA_OFFENTLIGE, PENSJON_ELLER_TRYGD, NAERINGSINNTEKT}

    private Long id;
    private InntektType inntektstype;
    private Double beloep;
    private LocalDate startOpptjeningsperiode;
    private LocalDate sluttOpptjeningsperiode;
    private Boolean inngaarIGrunnlagForTrekk;
    private Boolean utloeserArbeidsgiveravgift;
    private String fordel;
    private String skatteOgAvgiftsregel;
    private String skattemessigBosattILand;
    private String opptjeningsland;
    private String beskrivelse;
    private Tilleggsinformasjon tilleggsinformasjon;
    private Double antall;
    private String feilmelding;
}
