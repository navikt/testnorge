package no.nav.registre.arena.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NyBruker {

    @JsonProperty("personident")
    private String personident;

    @JsonProperty("miljoe")
    private String miljoe;

    @JsonProperty("kvalifiseringsgruppe")
    private String kvalifiseringsgruppe = "IKVAL";

    @JsonProperty("utenServicebehov")
    private UtenServiceBehov utenServiceBehov;

    @JsonProperty("automatiskInnsendingAvMeldekort")
    private boolean automatiskInnsendingAvMeldekort = true;

    @JsonProperty("aap115")
    private List<Aap115> aap115;

    @JsonProperty("aap")
    private List<Aap> aap;

}
