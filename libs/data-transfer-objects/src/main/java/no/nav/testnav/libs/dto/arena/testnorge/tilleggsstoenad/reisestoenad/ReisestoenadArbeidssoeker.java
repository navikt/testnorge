package no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.reisestoenad;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReisestoenadArbeidssoeker {

    @JsonAlias({ "KODE", "kode" })
    private ReisestoenadArbeidssoekerKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private ReisestoenadArbeidssoekerOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
