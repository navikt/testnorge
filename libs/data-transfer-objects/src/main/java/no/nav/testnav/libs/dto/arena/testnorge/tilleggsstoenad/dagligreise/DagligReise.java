package no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.dagligreise;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DagligReise {

    @JsonAlias({ "KODE", "kode" })
    private DagligReiseKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private DagligReiseOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
