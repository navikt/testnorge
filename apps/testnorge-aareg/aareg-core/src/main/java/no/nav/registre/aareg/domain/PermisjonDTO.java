package no.nav.registre.aareg.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    String startdato;
    @JsonProperty
    String sluttdato;
    @JsonProperty
    Float permisjonsprosent;
}
