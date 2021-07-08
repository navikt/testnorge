package no.nav.testnav.libs.dto.dokarkiv.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;


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
    @JsonProperty
    private String tittel;
    @JsonProperty
    private String kanal;
    @JsonProperty
    private String eksternReferanseId;
    @JsonProperty
    private Date datoMottatt;
    @JsonProperty
    private List<Dokument> dokumenter;

    public DokmotRequest(InntektDokument inntektDokument, byte[] pdf) {
        Dokumentvariant original = Dokumentvariant
                .builder()
                .filtype(inntektDokument.getMetadata().getFiltypeOriginal())
                .variantformat(inntektDokument.getMetadata().getVariantformatOriginal())
                .fysiskDokument(inntektDokument.getXml().getBytes(StandardCharsets.UTF_8))
                .build();
        Dokumentvariant arkiv = Dokumentvariant
                .builder()
                .filtype(inntektDokument.getMetadata().getFiltypeArkiv())
                .variantformat(inntektDokument.getMetadata().getVariantformatArkiv())
                .fysiskDokument(pdf)
                .build();

        this.journalposttype = inntektDokument.getMetadata().getJournalpostType();
        this.avsenderMottaker = new AvsenderMottaker(
                inntektDokument.getVirksomhetsnummer(),
                inntektDokument.getMetadata().getAvsenderMottakerIdType(),
                inntektDokument.getVirksomhetsnavn()
        );
        this.bruker = new Bruker(inntektDokument.getArbeidstakerFnr(), inntektDokument.getMetadata().getBrukerIdType());
        this.tema = inntektDokument.getMetadata().getTema();
        this.tittel = inntektDokument.getMetadata().getTittel();
        this.kanal = inntektDokument.getMetadata().getKanal();
        this.eksternReferanseId = inntektDokument.getMetadata().getEksternReferanseId();
        this.datoMottatt = inntektDokument.getDatoMottatt();
        this.dokumenter = Collections.singletonList(new Dokument(inntektDokument.getMetadata(), original, arkiv));
    }
}
