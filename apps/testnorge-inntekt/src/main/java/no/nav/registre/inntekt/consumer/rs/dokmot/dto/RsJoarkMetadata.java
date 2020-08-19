package no.nav.registre.inntekt.consumer.rs.dokmot.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@ApiModel
@Getter
@Setter
public class RsJoarkMetadata {

    private static final Random RANDOM = new Random();

    // Default values:
    private static final String DEFAULT_JOURNALPOST_TYPE = "INNGAAENDE";
    private static final String DEFAULT_AVSENDER_MOTTAKER_ID_TYPE = "ORGNR";
    private static final String DEFAULT_BRUKER_ID_TYPE = "FNR";
    private static final String DEFAULT_TEMA = "FOR";
    private static final String DEFAULT_TITTEL = "Syntetisk Inntektsmelding";
    private static final String DEFAULT_KANAL = "ALTINN";
    private static final String DEFAULT_FILTYPE_XML = "XML";
    private static final String DEFAULT_FILTYPE_PDF = "PDF";
    private static final String DEFAULT_VARIANTFORMAT_ORIGINAL = "ORIGINAL";
    private static final String DEFAULT_VARIANTFORMAT_ARKIV = "ARKIV";
    private static final String DEFAULT_DOKUMENTER_BREVKODE = "4936";
    private static final String DEFAULT_DOKUMENTER_BERVKATEGORI = "ES";

    private String journalpostType;
    private String avsenderMottakerIdType;
    private String brukerIdType;
    private String tema;
    private String tittel;
    private String kanal;
    private String eksternReferanseId;
    private String filtypeOriginal;
    private String filtypeArkiv;
    private String variantformatOriginal;
    private String variantformatArkiv;
    private String brevkode;
    private String brevkategori;

    public RsJoarkMetadata() {
        this.journalpostType = DEFAULT_JOURNALPOST_TYPE;
        this.avsenderMottakerIdType = DEFAULT_AVSENDER_MOTTAKER_ID_TYPE;
        this.brukerIdType = DEFAULT_BRUKER_ID_TYPE;
        this.tema = DEFAULT_TEMA;
        this.tittel = DEFAULT_TITTEL;
        this.kanal = DEFAULT_KANAL;
        this.eksternReferanseId = "AR" + randomNumber(7);
        this.filtypeOriginal = DEFAULT_FILTYPE_XML;
        this.filtypeArkiv = DEFAULT_FILTYPE_PDF;
        this.variantformatOriginal = DEFAULT_VARIANTFORMAT_ORIGINAL;
        this.variantformatArkiv = DEFAULT_VARIANTFORMAT_ARKIV;
        this.brevkode = DEFAULT_DOKUMENTER_BREVKODE;
        this.brevkategori = DEFAULT_DOKUMENTER_BERVKATEGORI;
    }

    private static long randomNumber(int length) {
        long min = (long) Math.pow(10, (length - 1));
        return min + RANDOM.nextInt((int)(min * 9));
    }
}
