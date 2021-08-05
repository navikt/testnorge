package no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.boutgift;

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
public class Boutgift {

    @JsonAlias({ "KODE", "kode" })
    private BoutgiftKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private BoutgiftOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
