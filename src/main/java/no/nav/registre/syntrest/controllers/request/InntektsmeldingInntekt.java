package no.nav.registre.syntrest.controllers.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InntektsmeldingInntekt {
    @JsonProperty
    private int aar;
    @JsonProperty
    private double beloep;
    @JsonProperty
    private String fnr;
    @JsonProperty
    private boolean inngaarIGrunnleggForTrekk;
    @JsonProperty
    private String inntektsinformasjonsType;
    @JsonProperty
    private String inntektstype;
    @JsonProperty
    private String maaned;
    @JsonProperty
    private boolean utloeserArbeidsgiveravgift;
    @JsonProperty
    private String virksomhet;
}
