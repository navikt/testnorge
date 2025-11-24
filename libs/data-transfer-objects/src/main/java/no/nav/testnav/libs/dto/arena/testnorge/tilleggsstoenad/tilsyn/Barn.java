package no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.tilsyn;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Barn {

    @JsonAlias({ "KODE", "kode" })
    private BarnKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private BarnOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
