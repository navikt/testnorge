package no.nav.registre.spion.provider.rs.request;


import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class SyntetiserSpionRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;
    @JsonProperty("miljoe")
    private String miljoe;
    @JsonProperty("startDato")
    private final LocalDate startDate;
    @JsonProperty("sluttDato")
    private final LocalDate endDate;
    @JsonProperty("antallPerioder")
    private final Integer numPeriods;

}
