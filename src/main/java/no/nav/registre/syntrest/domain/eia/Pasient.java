package no.nav.registre.syntrest.domain.eia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pasient {
    @JsonProperty
    String fornavn;
    @JsonProperty
    String mellomnavn;
    @JsonProperty
    String etternavn;
    @JsonProperty
    String fnr;
    @JsonProperty
    PasientAddresse addresse;
    @JsonProperty
    String tlf;
    @JsonProperty
    String fastlege;
    @JsonProperty("nav_kontor")
    String navKontor;
    @JsonProperty
    Arbeidsgiver arbeidsgiver;
}
