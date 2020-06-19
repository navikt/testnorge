package no.nav.registre.testnorge.synt.sykemelding.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.registre.testnorge.synt.sykemelding.consumer.dto.SyntSykemeldingHistorikkDTO;

public class SykemeldingListe {
    private final List<Sykemelding> liste;

    public SykemeldingListe(Map<SyntSykemeldingDTO, SyntSykemeldingHistorikkDTO> map, LegeListe legeListe) {
        liste = new ArrayList<>();
        map.forEach((sykemeldingDTO, historikk) -> liste.add(
                new Sykemelding(historikk, sykemeldingDTO, legeListe.getRandomLege())
        ));
    }


    public List<Sykemelding> getListe() {
        return liste;
    }
}
