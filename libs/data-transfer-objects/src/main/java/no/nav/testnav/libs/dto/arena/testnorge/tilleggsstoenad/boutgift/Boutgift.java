package no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.boutgift;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Boutgift {

    @JsonAlias({ "KODE", "kode" })
    private BoutgiftKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private BoutgiftOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
