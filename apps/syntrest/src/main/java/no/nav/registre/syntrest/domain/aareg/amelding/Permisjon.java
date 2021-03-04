package no.nav.registre.syntrest.domain.aareg.amelding;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Permisjon {

    @JsonAlias({"STARTDATO", "startdato"})
    private String startdato;

    @JsonAlias({"SLUTTDATO", "sluttdato"})
    private String sluttdato;

    @JsonAlias({"PERMISJONSPROSENT", "permisjonsprosent"})
    private float permisjonsprosent;

    @JsonAlias({"BESKRIVELSE", "beskrivelse"})
    private String beskrivelse;

}
