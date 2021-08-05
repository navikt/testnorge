package no.nav.testnav.libs.domain.dto.arena.testnorge.aap.gensaksopplysninger;

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
public class Saksopplysning {

    @JsonAlias({ "KODE", "kode" })
    private GensakKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private GensakOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
