package no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PermisjonDTO {
    @JsonProperty(required = true)
    String beskrivelse;
    @JsonProperty(required = true)
    LocalDate startdato;
    @JsonProperty(required = true)
    LocalDate sluttdato;
    @JsonProperty(required = true)
    Float permisjonsprosent;
}
