package no.nav.registre.arena.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NyBrukerFeil {
    String personident;
    String miljoe;
    NyBrukerFeilStatus nyBrukerFeilStatus;
    String melding;
}
