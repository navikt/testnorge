package no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.tilsyn;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class Barn {

    @JsonAlias({ "KODE", "kode" })
    private BarnKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private BarnOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
