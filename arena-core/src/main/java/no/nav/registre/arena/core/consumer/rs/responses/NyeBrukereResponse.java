package no.nav.registre.arena.core.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

import no.nav.registre.arena.domain.brukere.Arbeidsoeker;
import no.nav.registre.arena.domain.brukere.NyBrukerFeil;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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