package no.nav.registre.spion.provider.rs.request;


import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SyntetiserSpionRequest {

    @JsonProperty("avspillergruppeId")
    private final Long avspillergruppeId;
    @JsonProperty("miljoe")
    private final String miljoe;
    @JsonProperty("antallPersoner")
    private final Integer numPersons;
    @JsonProperty("startDato")
    private final LocalDate startDate;
    @JsonProperty("sluttDato")
    private final LocalDate endDate;
    @JsonProperty("antallPerioder")
    private final Integer numPeriods;

}
