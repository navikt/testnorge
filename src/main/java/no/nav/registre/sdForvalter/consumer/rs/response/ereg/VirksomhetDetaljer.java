package no.nav.registre.sdForvalter.consumer.rs.response.ereg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VirksomhetDetaljer {
    @JsonProperty
    private String enhetstype;
    @JsonProperty
    private LocalDate oppstartsdato;
    @JsonProperty
    private LocalDate virksomhetDetaljer;
}
