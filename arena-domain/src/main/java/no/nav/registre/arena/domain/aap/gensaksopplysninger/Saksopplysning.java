package no.nav.registre.arena.domain.aap.gensaksopplysninger;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class Saksopplysning {
    private GensakKoder kode;
    private GensakOvKoder overordnet;
    private String verdi;

    public Saksopplysning(GensakKoder kode, GensakOvKoder overordnet, String verdi) {
        this.overordnet = overordnet;
        this.kode = kode;
        this.verdi = verdi;
    }
}
