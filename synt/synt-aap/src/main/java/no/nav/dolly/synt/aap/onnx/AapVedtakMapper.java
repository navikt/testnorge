package no.nav.dolly.synt.aap.onnx;

import lombok.experimental.UtilityClass;
import no.nav.dolly.synt.aap.dto.AapVedtakDto;
import no.nav.dolly.synt.aap.dto.AatforAaunguforFriMkVedtakDto;
import no.nav.dolly.synt.aap.dto.Vedtak115Dto;

import java.util.List;
import java.util.Map;

@UtilityClass
class AapVedtakMapper {

    static AapVedtakDto toAapVedtak(Map<String, String> source) {
        return AapVedtakDto
                .builder()
                .aar(source.get("AAR"))
                .aktfasekode(source.get("AKTFASEKODE"))
                .andreOkonomYtelser(List.of())
                .avbruddskode(source.get("AVBRUDDSKODE"))
                .datoMottatt(source.get("DATO_MOTTATT"))
                .fraDato(source.get("FRA_DATO"))
                .genSaksopplysninger(List.of())
                .institusjonsopphold(List.of())
                .justertFra(source.get("JUSTERT_FRA"))
                .lopenrvedtak(source.get("LOPENRVEDTAK"))
                .medlemFolketrygden(List.of())
                .nyttkrav2(source.get("NYTTKRAV2"))
                .opprTilDato(source.get("OPPR_TIL_DATO"))
                .periode(List.of())
                .tilDato(source.get("TIL_DATO"))
                .utfall(source.get("UTFALL"))
                .vedtaksvariant(source.get("VEDTAKSVARIANT"))
                .vedtaktype(source.get("VEDTAKTYPE"))
                .vedtakDato(source.get("VEDTAK_DATO"))
                .vilkaar(List.of())
                .build();
    }

    static Vedtak115Dto toVedtak115(Map<String, String> source) {
        return Vedtak115Dto
                .builder()
                .datoMottatt(source.get("DATO_MOTTATT"))
                .fraDato(source.get("FRA_DATO"))
                .medisinskOpplysning(List.of())
                .tilDato(source.get("TIL_DATO"))
                .utfall(source.get("UTFALL"))
                .vedtaktype(source.get("VEDTAKTYPE"))
                .vilkaar(List.of())
                .build();
    }

    static AatforAaunguforFriMkVedtakDto toAatforAaunguforFriMkVedtak(Map<String, String> source) {
        return AatforAaunguforFriMkVedtakDto.builder()
                .avvbruddskode(source.get("AVVBRUDDSKODE"))
                .datoMottatt(source.get("DATO_MOTTATT"))
                .fraDato(source.get("FRA_DATO"))
                .utfall(source.get("UTFALL"))
                .utlandsmottaker(source.get("UTLANDSMOTTAKER"))
                .vedtaktype(source.get("VEDTAKTYPE"))
                .vilkaar(List.of())
                .build();
    }

}

