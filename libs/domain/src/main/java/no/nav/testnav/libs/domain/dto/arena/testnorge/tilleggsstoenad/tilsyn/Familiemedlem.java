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
public class Familiemedlem {

    @JsonAlias({ "KODE", "kode" })
    private FamiliemedlemKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private FamiliemedlemOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
