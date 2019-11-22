package no.nav.dolly.bestilling.inntektsstub.domain;

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
    private Double antall;
    private Double beloep;
    private String beskrivelse;
    private String feilmelding;
    private String fordel;
    private Boolean inngaarIGrunnlagForTrekk;
    private InntektType inntektstype;
    private String opptjeningsland;
    private String skatteOgAvgiftsregel;
    private String skattemessigBosattILand;
    private LocalDate sluttOpptjeningsperiode;
    private String startOpptjeningsperiode;
    private Tilleggsinformasjon tilleggsinformasjon;
    private Boolean utloeserArbeidsgiveravgift;
}
