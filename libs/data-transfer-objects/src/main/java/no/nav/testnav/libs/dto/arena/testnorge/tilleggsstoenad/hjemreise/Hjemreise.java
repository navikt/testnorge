package no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.hjemreise;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hjemreise {

    @JsonAlias({ "KODE", "kode" })
    private HjemreiseKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private HjemreiseOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
