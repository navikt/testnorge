package no.nav.registre.inntekt.consumer.rs.dokmot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import no.nav.registre.inntekt.domain.dokmot.InntektDokument;
import no.nav.registre.inntekt.utils.FileLoader;

@Value
public class DokmotRequest {

    @JsonProperty
    private String journalposttype;
    @JsonProperty
    private AvsenderMottaker avsenderMottaker;
    @JsonProperty
    private Bruker bruker;
    @JsonProperty
    private String tema;
    @JsonProperty("tittel")
    private String titel;
    @JsonProperty
    private String kanal;
    @JsonProperty
    private String eksternReferanseId;
    @JsonProperty
    private Date datoMottatt;
    @JsonProperty
    private List<Dokument> dokumenter;

    public DokmotRequest(InntektDokument inntektDokument) {
        Dokumentvariant orginal = Dokumentvariant
                .builder()
                .filtype("XML")
                .variantformat("ORIGINAL")
                .fysiskDokument(inntektDokument.getXml().getBytes(StandardCharsets.UTF_8))
                .build();
        Dokumentvariant arktiv = Dokumentvariant
                .builder()
                .filtype("PDF")
                .variantformat("ARKIV")
                .fysiskDokument(FileLoader.inst().getDummyPDF())
                .build();

        this.journalposttype = inntektDokument.getMetadata().getJournalpostType();
        this.avsenderMottaker = new AvsenderMottaker(
                inntektDokument.getVirksomhetsnummer(),
                inntektDokument.getMetadata().getAvsenderMottakerIdType(),
                inntektDokument.getVirksomhetsnavn()
        );
        this.bruker = new Bruker(inntektDokument.getArbeidstakerFnr(), inntektDokument.getMetadata().getBrukerIdType());
        this.tema = inntektDokument.getMetadata().getTema();
        this.titel = inntektDokument.getMetadata().getTitel();
        this.kanal = inntektDokument.getMetadata().getKanal();
        this.eksternReferanseId = inntektDokument.getMetadata().getEksternReferanseId();
        this.datoMottatt = inntektDokument.getDatoMottatt();
        this.dokumenter = Collections.singletonList(new Dokument(inntektDokument.getMetadata(), orginal, arktiv));
    }
}
