package no.nav.registre.spion.provider.rs.request;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class SyntetiserSpionRequest {
    @JsonProperty("startDato")
    private final String startDate;
    @JsonProperty("sluttDato")
    private final String endDate;
    @JsonProperty("antallPerioder")
    private final Integer numPeriods;

}
