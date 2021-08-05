package no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.dagligreise;

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
public class DagligReise {

    @JsonAlias({ "KODE", "kode" })
    private DagligReiseKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private DagligReiseOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
