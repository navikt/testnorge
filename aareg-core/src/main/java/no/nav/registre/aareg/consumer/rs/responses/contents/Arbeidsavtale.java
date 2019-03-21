package no.nav.registre.aareg.consumer.rs.responses.contents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arbeidsavtale {

    @JsonProperty("antallKonverterteTimer")
    private int antallKonverterteTimer;

    @JsonProperty("arbeidstidsordning")
    private String arbeidstidsordning;

    @JsonProperty("avloenningstype")
    private String avloenningstype;

    @JsonProperty("avtaltArbeidstimerPerUke")
    private Double avtaltArbeidstimerPerUke;

    @JsonProperty("endringsdatoStillingsprosent")
    private String endringsdatoStillingsprosent;

    @JsonProperty("sisteLoennsendringsdato")
    private String sisteLoennsendringsdato;

    @JsonProperty("stillingsprosent")
    private Double stillingsprosent;

    @JsonProperty("yrke")
    private String yrke;
}
