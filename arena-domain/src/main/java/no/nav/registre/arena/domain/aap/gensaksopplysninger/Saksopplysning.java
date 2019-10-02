package no.nav.registre.arena.domain.aap.gensaksopplysninger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Saksopplysning {
    private GensakKoder kode;
    private OverordnetV1 overordnet;
    private String verdi;

    public Saksopplysning(GensakKoder kode, String verdi) {
        overordnet = null;
        this.kode = kode;
        this.verdi = verdi;
    }
}
