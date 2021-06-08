package no.nav.registre.testnorge.libs.dto.dokarkiv.v1;

import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.inntektsmeldingservice.v1.response.InntektDokumentResponse;


@Value
public class ProsessertInntektDokument {
    String fnr;
    String journalpostId;
    Set<String> dokumentInfoId;
    String xml;

    public ProsessertInntektDokument(InntektDokument dokument, DokmotResponse response) {
        List<DokumentInfo> dokumentInfoListe = response.getDokumenter();
        this.fnr = dokument.getArbeidstakerFnr();
        this.journalpostId = response.getJournalpostId();
        this.dokumentInfoId = dokumentInfoListe == null
                ? Collections.emptySet()
                : dokumentInfoListe.stream().map(DokumentInfo::getDokumentInfoId).collect(Collectors.toSet());
        this.xml = dokument.getXml();
    }

    public InntektDokumentResponse toResponse() {
        return new InntektDokumentResponse(journalpostId, dokumentInfoId.isEmpty() ? null : dokumentInfoId.iterator().next(), xml);
    }
}
