package no.nav.registre.arena.domain.vedtak.forvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Konto {

    private String kontonr;
    private UtlandKontoInfo utland;
}
