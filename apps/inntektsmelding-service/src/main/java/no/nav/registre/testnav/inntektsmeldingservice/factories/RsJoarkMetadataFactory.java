package no.nav.registre.testnav.inntektsmeldingservice.factories;

import static java.util.Objects.nonNull;

import java.util.Map;

import no.nav.testnav.libs.dto.dokarkiv.v1.RsJoarkMetadata;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.RsInntektsmeldingRequest;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.YtelseKoder;

public class RsJoarkMetadataFactory {
    private RsJoarkMetadataFactory() {
    }

    private static final Map<YtelseKoder, String> ytelseMap = Map.of(
            YtelseKoder.FORELDREPENGER, "FOR",
            YtelseKoder.SVANGERSKAPSPENGER, "FOR",
            YtelseKoder.PLEIEPENGER, "OMS",
            YtelseKoder.PLEIEPENGER_BARN, "OMS",
            YtelseKoder.PLEIEPENGER_NAERSTAAENDE, "OMS",
            YtelseKoder.OMSORGSPENGER, "OMS",
            YtelseKoder.OPPLAERINGSPENGER, "OMS",
            YtelseKoder.SYKEPENGER, "SYK");

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
