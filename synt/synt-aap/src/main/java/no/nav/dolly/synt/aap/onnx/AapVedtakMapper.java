package no.nav.dolly.synt.aap.onnx;

import lombok.experimental.UtilityClass;
import no.nav.dolly.synt.aap.dto.AapVedtakDto;
import no.nav.dolly.synt.aap.dto.AatforAaunguforFriMkVedtakDto;
import no.nav.dolly.synt.aap.dto.MedisinskOpplysningDto;
import no.nav.dolly.synt.aap.dto.OpplysningDto;
import no.nav.dolly.synt.aap.dto.Vedtak115Dto;
import no.nav.dolly.synt.aap.dto.VilkaarDto;
import no.nav.dolly.synt.aap.dto.YtelseDto;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@UtilityClass
class AapVedtakMapper {

    private static final List<String> AAP_VILKAAR = List.of(
            "18-67AAR2", "MEDLSKAP12", "AOINORGE2", "INTKTBFAL2", "A11-52", "YTELARBGI2", "IVLGANNEN2", "AIANNEN2",
            "STRAFFGJF2", "AAFAARBIS2", "ABIDRARAK2", "AAIUBEGRF2", "AAMEDVPLA2", "AABEHGSTUD", "AASYKSKADE",
            "AAANNENSYK", "AAFERD2", "AAFOPTILUO", "AASAMMESYK", "AASYKFORUO", "AASYKTILUO", "UVUT2", "AALANGUTRE",
            "AASYKDOSKA", "AAOPPLTILT"
    );

    private static final List<String> VEDTAK_115_VILKAAR = List.of(
            "18-67AAR", "AAARBEVNE", "AAMOTTSAMT", "AANAAVJOBB", "AANODVDOKU", "AANORSKFER", "AASNARARBG",
            "AASNARTARB", "AATYPEJOBB", "INNTNEDS", "SYKSKADLYT"
    );

    private static final List<String> AATFOR_VILKAAR = List.of("MISBRUK", "PSYK", "SKADE");
    private static final List<String> AAUNGUFOR_VILKAAR = List.of("AAUNGNEDS", "AANEDSSL", "AASSLDOK", "AAFOR36");
    private static final List<String> FRI_MK_VILKAAR = List.of("FRI_MAAI", "FRI_MABH", "FRI_MAPL", "FRI_MARY", "FRI_MATI", "FRI_MAVI", "FRI_MAVU");

    private static final List<String> GEN_SAKSOPPLYSNINGER = List.of(
            "KDATO", "BTID", "TUUIN", "UUFOR", "STUBE", "OTILF", "OTSEK", "OOPPL_BDSAT", "OOPPL_UFTID", "OOPPL_BDSRP",
            "OOPPL_GRDRP", "OOPPL_GRDTU", "OOPPL_BDSTU", "OOPPL_GGRAD", "OOPPL_ORIGG", "YDATO", "YINNT", "YGRAD",
            "TLONN_SPROS", "TLONN_NORIN", "TLONN_FAKIN", "EOS", "LAND"
    );

    private static final List<String> INSTITUSJONSOPPHOLD = List.of("STRFG", "INSTA", "FRIKL", "INPER_INFRA", "INPER_INTIL", "REDPR", "INSUD");

    static AapVedtakDto toAapVedtak(Map<String, String> source) {
        return AapVedtakDto
                .builder()
                .aar(source.get("AAR"))
                .aktfasekode(source.get("AKTFASEKODE"))
                .andreOkonomYtelser(toAndreOkonomYtelser(source))
                .avbruddskode(source.get("AVBRUDDSKODE"))
                .datoMottatt(source.get("DATO_MOTTATT"))
                .fraDato(source.get("FRA_DATO"))
                .genSaksopplysninger(toOpplysninger(source, GEN_SAKSOPPLYSNINGER))
                .institusjonsopphold(toOpplysninger(source, INSTITUSJONSOPPHOLD))
                .justertFra(source.get("JUSTERT_FRA"))
                .lopenrvedtak(source.get("LOPENRVEDTAK"))
                .medlemFolketrygden(toMedlemFolketrygden(source))
                .nyttkrav2(source.get("NYTTKRAV2"))
                .opprTilDato(source.get("OPPR_TIL_DATO"))
                .periode(toPeriode(source))
                .tilDato(source.get("TIL_DATO"))
                .utfall(source.get("UTFALL"))
                .vedtaksvariant(source.get("VEDTAKSVARIANT"))
                .vedtaktype(source.get("VEDTAKTYPE"))
                .vedtakDato(source.get("VEDTAK_DATO"))
                .vilkaar(toVilkaar(source, AAP_VILKAAR))
                .build();
    }

    static Vedtak115Dto toVedtak115(Map<String, String> source) {
        return Vedtak115Dto
                .builder()
                .datoMottatt(source.get("DATO_MOTTATT"))
                .fraDato(source.get("FRA_DATO"))
                .medisinskOpplysning(toMedisinskOpplysning(source))
                .tilDato(source.get("TIL_DATO"))
                .utfall(source.get("UTFALL"))
                .vedtaktype(source.get("VEDTAKTYPE"))
                .vilkaar(toVilkaar(source, VEDTAK_115_VILKAAR))
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
                .vilkaar(toVilkaar(source, vedtakVilkaar(source.get("VEDTAKTYPE"), source)))
                .build();
    }

    private List<String> vedtakVilkaar(String vedtakType, Map<String, String> source) {
        if (source.containsKey("MISBRUK") || source.containsKey("PSYK") || source.containsKey("SKADE")) {
            return AATFOR_VILKAAR;
        }
        if (source.containsKey("AAUNGNEDS") || source.containsKey("AANEDSSL") || source.containsKey("AASSLDOK") || source.containsKey("AAFOR36")) {
            return AAUNGUFOR_VILKAAR;
        }
        if (source.containsKey("FRI_MAAI") || source.containsKey("FRI_MABH") || source.containsKey("FRI_MAPL")) {
            if (Objects.equals("E", vedtakType)) {
                return List.of("FRI_MAAI", "FRI_MARY", "FRI_MATI");
            }
            if (Objects.equals("S", vedtakType)) {
                return List.of("FRI_MABH", "FRI_MAPL", "FRI_MAVI", "FRI_MAVU");
            }
            return FRI_MK_VILKAAR;
        }
        return List.of();
    }

    private List<VilkaarDto> toVilkaar(Map<String, String> source, List<String> codes) {
        return codes
                .stream()
                .filter(source::containsKey)
                .map(code -> VilkaarDto.builder().kode(code).status(source.get(code)).build())
                .toList();
    }

    private List<MedisinskOpplysningDto> toMedisinskOpplysning(Map<String, String> source) {
        var hoved = MedisinskOpplysningDto.builder()
                .type(source.get("HOVED_TYPE"))
                .klassifisering(source.get("HOVED_KLASSIFISERING"))
                .diagnose(source.get("HOVED_DIAGNOSE"))
                .kilde(source.get("HOVED_KILDE"))
                .kildedato(source.get("HOVED_KILDEDATO"))
                .build();

        var bi = MedisinskOpplysningDto.builder()
                .type(source.get("BI_TYPE"))
                .klassifisering(source.get("BI_KLASSIFISERING"))
                .diagnose(source.get("BI_DIAGNOSE"))
                .kilde(source.get("BI_KILDE"))
                .kildedato(source.get("BI_KILDEDATO"))
                .build();

        if (isBlank(bi.getType()) && isBlank(bi.getDiagnose()) && isBlank(bi.getKlassifisering())) {
            return List.of(hoved);
        }
        return List.of(hoved, bi);
    }

    private List<YtelseDto> toMedlemFolketrygden(Map<String, String> source) {
        if (!source.containsKey("MEDL1")) {
            return List.of();
        }
        return List.of(YtelseDto.builder().kode("MEDL1").verdi(source.get("MEDL1")).build());
    }

    private List<Map<String, List<YtelseDto>>> toAndreOkonomYtelser(Map<String, String> source) {
        var values = Stream
                .of("TYPE", "FDATO", "TDATO", "GRAD", "BELOP", "BELPR")
                .filter(source::containsKey)
                .map(code -> YtelseDto.builder().kode(code).verdi(source.get(code)).build())
                .toList();
        if (values.isEmpty()) {
            return List.of();
        }
        return List.of(Map.of("ANNEN_OKONOM_YTELSE", values));
    }

    private List<OpplysningDto> toOpplysninger(Map<String, String> source, List<String> codes) {
        return codes
                .stream()
                .filter(source::containsKey)
                .map(code -> toOpplysning(code, source.get(code)))
                .toList();
    }

    private OpplysningDto toOpplysning(String code, String value) {
        var separator = code.indexOf('_');
        if (separator < 0) {
            return OpplysningDto.builder().kode(code).overordnet(null).verdi(value).build();
        }
        return OpplysningDto.builder()
                .kode(code.substring(separator + 1))
                .overordnet(code.substring(0, separator))
                .verdi(value)
                .build();
    }

    private List<YtelseDto> toPeriode(Map<String, String> source) {
        return Stream
                .of("PERIODE_KODE", "ENDRING_PERIODE", "ENDRING_PERIODE_BEGRUNNELSE", "NULLSTILL", "ENDRING_UNNTAK", "ENDRING_UNNTAK_BEGRUNNELSE")
                .filter(source::containsKey)
                .map(code -> YtelseDto.builder().kode(code).verdi(source.get(code)).build())
                .toList();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}

