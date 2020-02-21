package no.nav.registre.spion.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class SykemeldingInformation {

    @JsonProperty("startDato")
    private final String startDate;
    @JsonProperty("pasient")
    private final Patient patient;
    @JsonProperty("lege")
    private final Doctor doctor;

}