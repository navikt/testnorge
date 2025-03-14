package no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.flytting;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flytting {

    @JsonAlias({ "KODE", "kode" })
    private FlytteKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private FlytteOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
