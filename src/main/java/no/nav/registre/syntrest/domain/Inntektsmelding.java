package no.nav.registre.syntrest.Domain;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Inntektsmelding {
    @NotNull
    private String aar;
    @NotNull
    private double beloep;
    @NotNull
    private boolean inngaarIGrunnlagForTrekk;
    @NotNull
    private String inntektsType;
    @NotNull
    private String maaned;
    @NotNull
    private boolean utloeserArbeidsgiveravgift;
    @NotNull
    private int virksomhet;
}
