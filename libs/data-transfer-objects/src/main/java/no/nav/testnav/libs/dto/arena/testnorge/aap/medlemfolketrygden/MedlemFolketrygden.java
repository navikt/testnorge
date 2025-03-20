package no.nav.testnav.libs.dto.arena.testnorge.aap.medlemfolketrygden;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MedlemFolketrygden {

    @JsonAlias({ "KODE", "kode" })
    private FolketrygdenKoder kode;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
