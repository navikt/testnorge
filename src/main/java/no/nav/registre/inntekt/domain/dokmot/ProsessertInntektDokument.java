package no.nav.registre.inntekt.domain.dokmot;

import lombok.Value;

import no.nav.registre.inntekt.consumer.rs.dokmot.dto.DokmotResponse;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.DokumentInfo;

import java.util.List;

@Value
public class ProsessertInntektDokument {
    private final String fnr;
    private final String journalpostId;
    private final String dokumentInfoId;
    private final String xml;

    public ProsessertInntektDokument(InntektDokument dokument, DokmotResponse response) {
        List<DokumentInfo> dokumentInfoListe = response.getDokumenter();

        this.fnr = dokument.getArbeidstakerFnr();
        this.journalpostId = response.getJournalpostId();

        this.dokumentInfoId = dokumentInfoListe != null && !dokumentInfoListe.isEmpty() ?
                dokumentInfoListe.get(0).getDokumentInfoId() : null;
        this.xml = dokument.getXml();
    }
}
