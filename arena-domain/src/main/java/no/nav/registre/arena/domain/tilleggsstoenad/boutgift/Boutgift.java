package no.nav.registre.arena.domain.tilleggsstoenad.boutgift;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Boutgift {

    @JsonAlias({ "KODE", "kode" })
    private BoutgiftKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private BoutgiftOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
