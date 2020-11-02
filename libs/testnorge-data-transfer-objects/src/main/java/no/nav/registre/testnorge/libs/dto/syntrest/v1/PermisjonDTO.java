package no.nav.registre.testnorge.libs.dto.syntrest.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PermisjonDTO {
    @JsonProperty("BESKRIVELSE")
    String beskrivelse;
    @JsonProperty("PERMISJONSPROSENT")
    String permisjonsprosent;
    @JsonProperty("STARTDATO")
    LocalDate startdato;
    @JsonProperty("SLUTTDATO")
    LocalDate sluttdato;
}
