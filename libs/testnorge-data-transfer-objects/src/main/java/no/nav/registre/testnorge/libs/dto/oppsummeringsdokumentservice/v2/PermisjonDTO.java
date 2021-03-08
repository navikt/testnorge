package no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PermisjonDTO {
    @JsonProperty
    String permisjonId;
    @JsonProperty
    String beskrivelse;
    @JsonProperty
    LocalDate startdato;
    @JsonProperty
    LocalDate sluttdato;
    @JsonProperty
    Float permisjonsprosent;
}
