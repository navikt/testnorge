package no.nav.registre.arena.domain.tilleggsstoenad.hjemreise;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Hjemreise {

    @JsonAlias({ "KODE", "kode" })
    private HjemreiseKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private HjemreiseOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
