package no.nav.registre.sdForvalter.consumer.rs.response.ereg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonDetaljer {
    @JsonProperty
    private final List<EregAdresse> forretningsadresser;
    @JsonProperty
    private final List<EregAdresse> postadresser;
}
