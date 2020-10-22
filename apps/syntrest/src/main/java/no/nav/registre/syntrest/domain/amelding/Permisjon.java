package no.nav.registre.syntrest.domain.amelding;

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

    @JsonAlias({"STARTDATO", "startdato", "startDato"})
    private String startdato;

    @JsonAlias({"SLUTTDATO", "sluttdato", "sluttDato"})
    private String sluttdato;

    @JsonAlias({"PERMISJONSPROSENT", "permisjonsprosent"})
    private String permisjonsprosent;

    @JsonAlias({"BESKRIVELSE", "beskrivelse"})
    private String beskrivelse;

}
