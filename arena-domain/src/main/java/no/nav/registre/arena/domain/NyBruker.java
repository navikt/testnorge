package no.nav.registre.arena.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.domain.aap.Aap;
import no.nav.registre.arena.domain.aap115.Aap115;

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
    private List<Aap115> aap115;
    private List<Aap> aap;
}
