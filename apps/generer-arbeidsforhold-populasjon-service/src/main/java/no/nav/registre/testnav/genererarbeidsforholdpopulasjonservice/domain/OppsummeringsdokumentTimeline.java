package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import java.util.List;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Oppsummeringsdokument;

public class OppsummeringsdokumentTimeline extends Timeline<Oppsummeringsdokument> {

    public OppsummeringsdokumentTimeline(List<Oppsummeringsdokument> list) {
        list.forEach(this::put);
    }

    private void put(Oppsummeringsdokument oppsummeringsdokument) {
        super.put(oppsummeringsdokument.getKalendermaaned(), oppsummeringsdokument);
    }
}
