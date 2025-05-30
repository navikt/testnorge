package no.nav.testnav.libs.dto.arena.testnorge.vedtak.forvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Forvalter {

    private Konto gjeldendeKontonr;
    private Adresse utbetalingsadresse;
}
