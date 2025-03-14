package no.nav.testnav.libs.dto.arena.testnorge.aap.institusjonsopphold;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institusjonsopphold {

    @JsonAlias({ "KODE", "kode" })
    private InstKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private InstOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
