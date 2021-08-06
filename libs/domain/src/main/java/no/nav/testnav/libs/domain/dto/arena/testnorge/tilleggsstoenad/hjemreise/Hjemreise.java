package no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.hjemreise;

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
public class Hjemreise {

    @JsonAlias({ "KODE", "kode" })
    private HjemreiseKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private HjemreiseOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
