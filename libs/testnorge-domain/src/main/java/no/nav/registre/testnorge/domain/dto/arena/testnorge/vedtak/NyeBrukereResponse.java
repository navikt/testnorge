package no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyBrukerFeil;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NyeBrukereResponse {

    @JsonProperty("arbeidsokerList")
    private List<Arbeidsoeker> arbeidsoekerList;

    @JsonProperty("nyBrukerFeilList")
    private List<NyBrukerFeil> nyBrukerFeilList;

    @JsonProperty("antallSider")
    private Integer antallSider;

    public List<Arbeidsoeker> getArbeidsoekerList() {
        return arbeidsoekerList != null ? arbeidsoekerList : Collections.emptyList();
    }

    public List<NyBrukerFeil> getNyBrukerFeilList() {
        return nyBrukerFeilList != null ? nyBrukerFeilList : Collections.emptyList();
    }
}