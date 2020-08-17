package no.nav.registre.inntekt.consumer.rs.dokmot.dto;

import lombok.Value;

import no.nav.registre.inntekt.consumer.rs.dokmot.dto.DokmotResponse;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.DokumentInfo;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.InntektDokument;

import java.util.List;

@Value
public class ProsessertInntektDokument {
    private final String fnr;
    private final String journalpostId;
    private final String dokumentInfoId;
    private final String xml;

    // TODO: v2, endre dokumentInfoId til en liste som har alle dokumentInfoId'er tilknyttet journalposten.
    public ProsessertInntektDokument(InntektDokument dokument, DokmotResponse response) {
        List<DokumentInfo> dokumentInfoListe = response.getDokumenter();

        this.fnr = dokument.getArbeidstakerFnr();
        this.journalpostId = response.getJournalpostId();
        this.dokumentInfoId = dokumentInfoListe.get(0).getDokumentInfoId();
        this.xml = dokument.getXml();
    }
}
