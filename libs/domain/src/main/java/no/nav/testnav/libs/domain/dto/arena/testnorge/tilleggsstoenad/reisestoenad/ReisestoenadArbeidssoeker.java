package no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.reisestoenad;

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
public class ReisestoenadArbeidssoeker {

    @JsonAlias({ "KODE", "kode" })
    private ReisestoenadArbeidssoekerKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private ReisestoenadArbeidssoekerOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
