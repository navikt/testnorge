package no.nav.registre.inntekt.factories;

import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.RsInntektsmeldingRequest;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.enums.YtelseKodeListe;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.RsJoarkMetadata;

import java.util.Map;

import static java.util.Objects.nonNull;

public class RsJoarkMetadataFactory {
    private RsJoarkMetadataFactory() {
    }

    private static final Map<YtelseKodeListe, String> ytelseMap = Map.of(
            YtelseKodeListe.FORELDREPENGER, "FOR",
            YtelseKodeListe.SVANGERSKAPSPENGER, "FOR",
            YtelseKodeListe.PLEIEPENGER, "OMS",
            YtelseKodeListe.PLEIEPENGER_BARN, "OMS",
            YtelseKodeListe.PLEIEPENGER_NAERSTAAENDE, "OMS",
            YtelseKodeListe.OMSORGSPENGER, "OMS",
            YtelseKodeListe.OPPLAERINGSPENGER, "OMS",
            YtelseKodeListe.SYKEPENGER, "SYK");

    private static final String DEFAULT_JOURNALPOST_TYPE = "INNGAAENDE";
    private static final String DEFAULT_BRUKER_ID_TYPE = "FNR";
    private static final String DEFAULT_TITTEL = "Syntetisk Inntektsmelding";
    private static final String DEFAULT_KANAL = "ALTINN";
    private static final String DEFAULT_FILTYPE_XML = "XML";
    private static final String DEFAULT_FILTYPE_PDF = "PDF";
    private static final String DEFAULT_VARIANTFORMAT_ORIGINAL = "ORIGINAL";
    private static final String DEFAULT_VARIANTFORMAT_ARKIV = "ARKIV";
    private static final String DEFAULT_DOKUMENTER_BREVKODE = "4936";
    private static final String DEFAULT_DOKUMENTER_BERVKATEGORI = "ES";

    public static RsJoarkMetadata create(RsInntektsmeldingRequest inntektsmelding, Long id) {
        return RsJoarkMetadata.builder()
                .journalpostType(DEFAULT_JOURNALPOST_TYPE)
                .avsenderMottakerIdType(nonNull(inntektsmelding.getArbeidsgiver()) ? "ORGNR" : "FNR")
                .brukerIdType(DEFAULT_BRUKER_ID_TYPE)
                .tema(ytelseMap.get(inntektsmelding.getYtelse()))
                .tittel(DEFAULT_TITTEL)
                .kanal(DEFAULT_KANAL)
                .eksternReferanseId("AR" + padLeftZeros(String.valueOf(id), 7))
                .filtypeOriginal(DEFAULT_FILTYPE_XML)
                .filtypeArkiv(DEFAULT_FILTYPE_PDF)
                .variantformatOriginal(DEFAULT_VARIANTFORMAT_ORIGINAL)
                .variantformatArkiv(DEFAULT_VARIANTFORMAT_ARKIV)
                .brevkode(DEFAULT_DOKUMENTER_BREVKODE)
                .brevkategori(DEFAULT_DOKUMENTER_BERVKATEGORI)
                .build();
    }

    private static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);
        return sb.toString();
    }
}
