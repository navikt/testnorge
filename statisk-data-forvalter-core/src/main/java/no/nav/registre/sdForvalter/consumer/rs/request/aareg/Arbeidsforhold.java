package no.nav.registre.sdForvalter.consumer.rs.request.aareg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Arbeidsforhold {

    @JsonProperty("arbeidsgiver")
    private Arbeidsgiver arbeidsgiver;

    @JsonProperty("arbeidstaker")
    private Arbeidstaker arbeidstaker;

}
