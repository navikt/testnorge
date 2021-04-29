package no.nav.registre.testnorge.libs.dto.ameldingservice.v1;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @JsonProperty
    List<AvvikDTO> avvik;

    public List<AvvikDTO> getAvvik() {
        if (avvik == null) {
            avvik = new ArrayList<>();
        }
        return avvik;
    }
}
