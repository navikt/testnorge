package no.nav.registre.testnorge.domain.dto.arena.testnorge.tilleggsstoenad.flytting;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Flytting {

    @JsonAlias({ "KODE", "kode" })
    private FlytteKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private FlytteOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
