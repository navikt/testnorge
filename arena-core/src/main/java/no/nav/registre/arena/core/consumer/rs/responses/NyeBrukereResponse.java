package no.nav.registre.arena.core.consumer.rs.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.arena.domain.Arbeidsoeker;
import no.nav.registre.arena.domain.NyBrukerFeil;

@Getter
@Setter
public class NyeBrukereResponse {
    List<Arbeidsoeker> arbeidsoekerList;
    List<NyBrukerFeil> nyBrukerFeilList;

    public NyeBrukereResponse() {
        this.arbeidsoekerList = new ArrayList<>();
        this.nyBrukerFeilList = new ArrayList<>();
    }
}