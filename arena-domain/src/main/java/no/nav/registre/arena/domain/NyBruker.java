package no.nav.registre.arena.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class NyBruker {

    @JsonProperty("personident")
    private String personident;

    @JsonProperty("miljoe")
    private String miljoe;

    @JsonProperty("kvalifiseringsgruppe")
    private String kvalifiseringsgruppe;

    @JsonProperty("utenServicebehov")
    private UtenServiceBehov utenServiceBehov;

    @JsonProperty("automatiskInnsendingAvMeldekort")
    private boolean automatiskInnsendingAvMeldekort;

    @JsonProperty("aap115")
    private List<Aap115> aap115;

    @JsonProperty("aap")
    private List<Aap> aap;

}
