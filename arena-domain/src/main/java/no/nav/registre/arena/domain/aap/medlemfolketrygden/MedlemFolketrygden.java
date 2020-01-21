package no.nav.registre.arena.domain.aap.medlemfolketrygden;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MedlemFolketrygden {

    @JsonAlias({ "KODE", "kode" })
    private FolketrygdenKoder kode;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
