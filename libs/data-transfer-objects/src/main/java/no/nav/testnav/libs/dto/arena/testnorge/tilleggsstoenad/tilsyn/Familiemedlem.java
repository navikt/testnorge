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
public class Familiemedlem {

    @JsonAlias({ "KODE", "kode" })
    private FamiliemedlemKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private FamiliemedlemOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
