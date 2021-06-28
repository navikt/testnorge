package no.nav.registre.aareg.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
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
