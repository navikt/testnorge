package no.nav.registre.aareg.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import no.nav.registre.aareg.util.JsonDateDeserializer;
import no.nav.registre.aareg.util.JsonDateSerializer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arbeidsavtale {

    @JsonProperty("antallKonverterteTimer")
    private Integer antallKonverterteTimer;

    @JsonProperty("arbeidstidsordning")
    private String arbeidstidsordning;

    @JsonProperty("avloenningstype")
    private String avloenningstype;

    @JsonProperty("avtaltArbeidstimerPerUke")
    private Double avtaltArbeidstimerPerUke;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonProperty("endringsdatoStillingsprosent")
    private LocalDateTime endringsdatoStillingsprosent;

    @JsonProperty("stillingsprosent")
    private Double stillingsprosent;

    @JsonProperty("yrke")
    private String yrke;
}
