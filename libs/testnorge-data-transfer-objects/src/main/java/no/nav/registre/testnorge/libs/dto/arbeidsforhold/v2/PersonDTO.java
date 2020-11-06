package no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonDTO {
    @JsonProperty(required = true)
    private final String ident;
    @JsonProperty(required = true)
    private List<ArbeidsforholdDTO> arbeidsforhold;
}
