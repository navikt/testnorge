package no.nav.registre.testnorge.domain.dto.arena.testnorge.tilleggsstoenad.laeremidler;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Laeremiddel {

    @JsonAlias({ "KODE", "kode" })
    private LaeremiddelKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private LaeremiddelOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
