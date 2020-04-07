package no.nav.registre.inntekt.domain.dokmot;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Value;

@ApiModel
@Value
public class RsJoarkMetadata {
    // Default values:
    private static final String JOURNALPOST_TYPE = "INNGAAENDE";
    private static final String AVSENDER_MOTTAKER_ID_TYPE = "ORGNR";
    private static final String BRUKER_ID_TYPE = "FNR";
    private static final String TEMA = "FOR";
    private static final String TITTEL = "Syntetisk Inntektsmelding";
    private static final String KANAL = "ALTINN";
    private static final String EKSTERN_REFERANSE_ID = "INGEN";
    private static final String FILTYPE_XML = "XML";
    private static final String FILTYPE_PDF = "PDF";
    private static final String VARIANTFORMAT_ORIGINAL = "ORIGINAL";
    private static final String VARIANTFORMAT_ARKIV = "ARKIV";
    private static final String DOKUMENTER_BREVKODE = "4936";
    private static final String DOKUMENTER_BERVKATEGORI = "ES";

    @JsonProperty(defaultValue = JOURNALPOST_TYPE)
    private String journalpostType;
    @JsonProperty(defaultValue = AVSENDER_MOTTAKER_ID_TYPE)
    private String avsenderMottakerIdType;
    @JsonProperty(defaultValue = BRUKER_ID_TYPE)
    private String brukerIdType;
    @JsonProperty(defaultValue = TEMA)
    private String tema;
    @JsonProperty(defaultValue = TITTEL)
    private String tittel;
    @JsonProperty(defaultValue = KANAL)
    private String kanal;
    @JsonProperty(defaultValue = EKSTERN_REFERANSE_ID)
    private String eksternReferanseId;
    @JsonProperty(defaultValue = FILTYPE_XML)
    private String filtypeOriginal;
    @JsonProperty(defaultValue = FILTYPE_PDF)
    private String filtypeArkiv;
    @JsonProperty(defaultValue = VARIANTFORMAT_ORIGINAL)
    private String variantformatOriginal;
    @JsonProperty(defaultValue = VARIANTFORMAT_ARKIV)
    private String variantformatArkiv;
    @JsonProperty(defaultValue = DOKUMENTER_BREVKODE)
    private String brevkode;
    @JsonProperty(defaultValue = DOKUMENTER_BERVKATEGORI)
    private String brevkategori;

    /**
     * @SuppressWarnings used by jackson
     */
    @SuppressWarnings("unused")
    public RsJoarkMetadata() {
        this.journalpostType = JOURNALPOST_TYPE;
        this.avsenderMottakerIdType = AVSENDER_MOTTAKER_ID_TYPE;
        this.brukerIdType = BRUKER_ID_TYPE;
        this.tema = TEMA;
        this.tittel = TITTEL;
        this.kanal = KANAL;
        this.eksternReferanseId = EKSTERN_REFERANSE_ID;
        this.filtypeOriginal = FILTYPE_XML;
        this.filtypeArkiv = FILTYPE_PDF;
        this.variantformatOriginal = VARIANTFORMAT_ORIGINAL;
        this.variantformatArkiv = VARIANTFORMAT_ARKIV;
        this.brevkode = DOKUMENTER_BREVKODE;
        this.brevkategori = DOKUMENTER_BERVKATEGORI;
    }
}
