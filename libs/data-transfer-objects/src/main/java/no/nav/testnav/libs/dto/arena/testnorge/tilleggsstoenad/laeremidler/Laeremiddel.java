package no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.laeremidler;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Laeremiddel {

    @JsonAlias({ "KODE", "kode" })
    private LaeremiddelKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private LaeremiddelOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
