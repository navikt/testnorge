package no.nav.testnav.libs.domain.dto.arena.testnorge.brukere;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NyBruker {

    private String personident;
    private String miljoe;
    private Kvalifiseringsgrupper kvalifiseringsgruppe;
    private UtenServicebehov utenServicebehov;
    private Boolean automatiskInnsendingAvMeldekort;
    private String oppfolging;
    private LocalDate aktiveringsDato;
}
