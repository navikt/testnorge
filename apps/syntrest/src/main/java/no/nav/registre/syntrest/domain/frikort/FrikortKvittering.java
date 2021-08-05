package no.nav.registre.syntrest.domain.frikort;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FrikortKvittering {

    @JsonProperty
    String betalt;
    @JsonProperty
    String dato_mottatt;
    @JsonProperty
    String datotjeneste;
    @JsonProperty
    String datotjenestestart;
    @JsonProperty
    float egenandelsats;
    @JsonProperty
    String egenandelskode;
    @JsonProperty
    String enkeltregningsstatuskode;
    @JsonProperty
    String innsendingstypekode;
    @JsonProperty
    String kildesystemkode;
    @JsonProperty
    String merknader;
    @JsonProperty
    String samhandlertypekode;
}
