package no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonDTO {
    @JsonProperty(required = true)
    String ident;
    @JsonProperty(required = true)
    List<ArbeidsforholdDTO> arbeidsforhold;
}
