package no.nav.registre.spion.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SykemeldingInformation {

    @JsonProperty("startDato")
    private String startDate;
    @JsonProperty("pasient")
    private Patient patient;
    @JsonProperty("lege")
    private Doctor doctor;

}