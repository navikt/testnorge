package no.nav.registre.inntekt.consumer.rs.dokmot.dto;

import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.inntekt.provider.rs.v1.response.InntektDokumentResponseV1;

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

    public InntektDokumentResponseV1 toResponseV1() {
        return new InntektDokumentResponseV1(journalpostId, dokumentInfoId.isEmpty() ? null : dokumentInfoId.iterator().next(), xml);
    }
}
