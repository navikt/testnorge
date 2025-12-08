package no.nav.dolly.bestilling.inntektstub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Inntekt {

    public enum InntektType {LOENNSINNTEKT, YTELSE_FRA_OFFENTLIGE, PENSJON_ELLER_TRYGD, NAERINGSINNTEKT}

    @EqualsAndHashCode.Exclude
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

    @EqualsAndHashCode.Exclude
    private String feilmelding;
}
