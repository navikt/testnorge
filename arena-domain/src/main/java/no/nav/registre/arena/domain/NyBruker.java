package no.nav.registre.arena.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NyBruker {

    private String personident;
    private String miljoe;
    private String kvalifiseringsgruppe;
    private UtenServicebehov utenServicebehov;
    private Boolean automatiskInnsendingAvMeldekort;
}
