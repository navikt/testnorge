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
        String dokumentInfoIdTmp;

        this.fnr = dokument.getArbeidstakerFnr();
        this.journalpostId = response.getJournalpostId();
        List<DokumentInfo> tmp = response.getDokumenter();
        dokumentInfoIdTmp = null;
        if (!tmp.isEmpty()) {
            // Inntektsmeldinger har bare ett dokument per journalpost.
            dokumentInfoIdTmp = tmp.get(0).getDokumentInfoId();
        }
        this.dokumentInfoId = dokumentInfoIdTmp;
        this.xml = dokument.getXml();
    }
}
