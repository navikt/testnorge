package no.nav.registre.elsam.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pasient {

    private String fornavn;
    private String mellomnavn;
    private String etternavn;

    private String fnr;
    private String tlf;

    private String fastlege;
    @JsonProperty("nav_kontor")
    private String navKontor;
    private Arbeidsgiver arbeidsgiver;
}
