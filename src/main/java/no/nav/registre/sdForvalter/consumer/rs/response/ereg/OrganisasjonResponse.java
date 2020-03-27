package no.nav.registre.sdForvalter.consumer.rs.response.ereg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonResponse {
    @JsonProperty
    private String organisasjonsnummer;
    @JsonProperty
    private VirksomhetDetaljer virksomhetDetaljer;
    @JsonProperty
    private Navn navn;
}
