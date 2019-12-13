package no.nav.registre.arena.domain.aap.institusjonsopphold;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Institusjonsopphold {

    @JsonAlias({ "KODE", "kode" })
    private InstKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private InstOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
