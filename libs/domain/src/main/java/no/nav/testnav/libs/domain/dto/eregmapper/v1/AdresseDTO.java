package no.nav.testnav.libs.domain.dto.eregmapper.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdresseDTO {
    @JsonProperty
    private List<String> adresser;
    @JsonProperty
    private String postnr;
    @JsonProperty
    private String kommunenr;
    @JsonProperty
    private String landkode;
    @JsonProperty
    private String poststed;
}
