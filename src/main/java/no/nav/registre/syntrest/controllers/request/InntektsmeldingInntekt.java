package no.nav.registre.syntrest.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InntektsmeldingInntekt {
    private int aar;
    private double beloep;
    private String fnr;
    private boolean inngaarIGrunnleggForTrekk;
    private String inntektsinformasjonsType;
    private String inntektstype;
    private String maaned;
    private boolean utloeserArbeidsgiveravgift;
    private String virksomhet;
}
