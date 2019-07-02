package no.nav.registre.arena.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NyBruker {
    @JsonProperty
    private String personident;
    @JsonProperty
    private String miljoe;
    @JsonProperty
    private String kvalifiseringsgruppe; // "IKVAL"
    @JsonProperty
    private UtenServicebehov utenServicebehov;
    @JsonProperty
    private Boolean automatiskInnsendingAvMeldekort; // true
    @JsonProperty
    private List<Aap115> aap115;
    @JsonProperty
    private List<Aap> aap;

}
