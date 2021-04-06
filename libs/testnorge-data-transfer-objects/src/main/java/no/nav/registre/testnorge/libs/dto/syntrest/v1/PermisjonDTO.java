package no.nav.registre.testnorge.libs.dto.syntrest.v1;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias("BESKRIVELSE")
    String beskrivelse;
    @JsonAlias("PERMISJONSPROSENT")
    String permisjonsprosent;
    @JsonAlias("STARTDATO")
    LocalDate startdato;
    @JsonAlias("SLUTTDATO")
    LocalDate sluttdato;
    AvvikDTO avvik;
}
