package no.nav.registre.arena.domain.aap.gensaksopplysninger;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class Saksopplysning {

    @JsonAlias({ "KODE", "kode" })
    private GensakKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private GensakOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;

    public Saksopplysning(GensakKoder kode, GensakOvKoder overordnet, String verdi) {
        this.overordnet = overordnet;
        this.kode = kode;
        this.verdi = verdi;
    }
}
