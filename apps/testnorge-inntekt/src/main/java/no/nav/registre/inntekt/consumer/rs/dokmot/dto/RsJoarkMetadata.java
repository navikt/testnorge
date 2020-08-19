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
    private static final String JOURNALPOST_TYPE = "INNGAAENDE";
    private static final String AVSENDER_MOTTAKER_ID_TYPE = "ORGNR";
    private static final String BRUKER_ID_TYPE = "FNR";
    private static final String TEMA = "FOR";
    private static final String TITTEL = "Syntetisk Inntektsmelding";
    private static final String KANAL = "ALTINN";
    private static final String FILTYPE_XML = "XML";
    private static final String FILTYPE_PDF = "PDF";
    private static final String VARIANTFORMAT_ORIGINAL = "ORIGINAL";
    private static final String VARIANTFORMAT_ARKIV = "ARKIV";
    private static final String DOKUMENTER_BREVKODE = "4936";
    private static final String DOKUMENTER_BERVKATEGORI = "ES";

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
        this.journalpostType = JOURNALPOST_TYPE;
        this.avsenderMottakerIdType = AVSENDER_MOTTAKER_ID_TYPE;
        this.brukerIdType = BRUKER_ID_TYPE;
        this.tema = TEMA;
        this.tittel = TITTEL;
        this.kanal = KANAL;
        this.eksternReferanseId = "AR" + randomNumber(7);
        this.filtypeOriginal = FILTYPE_XML;
        this.filtypeArkiv = FILTYPE_PDF;
        this.variantformatOriginal = VARIANTFORMAT_ORIGINAL;
        this.variantformatArkiv = VARIANTFORMAT_ARKIV;
        this.brevkode = DOKUMENTER_BREVKODE;
        this.brevkategori = DOKUMENTER_BERVKATEGORI;
    }

    private static long randomNumber(int length) {
        int min = (int)Math.pow(10, length-1);
        return min + RANDOM.nextInt(min*9);
    }
}
