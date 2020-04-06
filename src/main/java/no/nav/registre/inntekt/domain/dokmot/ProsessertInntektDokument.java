package no.nav.registre.inntekt.domain.dokmot;

import lombok.Value;

import no.nav.registre.inntekt.consumer.rs.dokmot.dto.DokmotResponse;

@Value
public class ProsessertInntektDokument {
    private final String fnr;
    private final String journalpostId;
    private final String xml;

    public ProsessertInntektDokument(InntektDokument dokument, DokmotResponse response) {
        this.fnr = dokument.getArbeidstakerFnr();
        this.journalpostId = response.getJournalpostId();
        this.xml = dokument.getXml();
    }
}
