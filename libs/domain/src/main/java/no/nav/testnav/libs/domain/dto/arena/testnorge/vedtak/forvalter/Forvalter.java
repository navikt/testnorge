package no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.forvalter;

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
public class Forvalter {

    private Konto gjeldendeKontonr;
    private Adresse utbetalingsadresse;
}
