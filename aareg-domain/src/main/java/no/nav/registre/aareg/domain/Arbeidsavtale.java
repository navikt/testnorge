package no.nav.registre.aareg.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Arbeidsavtale {

    @JsonProperty("antallKonverterteTimer")
    private Integer antallKonverterteTimer;

    @JsonProperty("arbeidstidsordning")
    private String arbeidstidsordning;

    @JsonProperty("avloenningstype")
    private String avloenningstype;

    @JsonProperty("avtaltArbeidstimerPerUke")
    private Double avtaltArbeidstimerPerUke;

    @JsonProperty("endringsdatoStillingsprosent")
    private String endringsdatoStillingsprosent;

    @JsonProperty("stillingsprosent")
    private Double stillingsprosent;

    @JsonProperty("yrke")
    private String yrke;
}
