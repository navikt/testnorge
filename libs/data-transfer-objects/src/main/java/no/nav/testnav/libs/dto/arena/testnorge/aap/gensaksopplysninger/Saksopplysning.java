package no.nav.testnav.libs.dto.arena.testnorge.aap.gensaksopplysninger;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
