package no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.WhitespaceConstants.DUMMY_DATO;
import static no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.WhitespaceConstants.DUMMY_IDENT;
import static no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.WhitespaceConstants.FIVE_OES;
import static no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.WhitespaceConstants.SIX_OES;
import static no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.WhitespaceConstants.WHITESPACE_100_STK;
import static no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.WhitespaceConstants.WHITESPACE_25_STK;
import static no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.WhitespaceConstants.WHITESPACE_30_STK;
import static no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.WhitespaceConstants.WHITESPACE_40_STK;
import static no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.WhitespaceConstants.WHITESPACE_50_STK;

@Getter
public enum SkdFeltDefinisjonerTrans1 {
    FODSELSDATO("fodselsdato", SIX_OES, 1, 6, 1, 6),
    PERSONNUMMER("personnummer", FIVE_OES, 2, 5, 7, 11),
    MASKINDATO("maskindato", DUMMY_DATO, 3, 8, 12, 19),
    MASKINTID("maskintid", SIX_OES, 4, 6, 20, 25),
    TRANSTYPE("transtype", "0", 5, 1, 26, 26),
    AARSAKSKODE("aarsakskode", "00", 6, 2, 27, 28),
    REG_DATO("regDato", DUMMY_DATO, 7, 8, 29, 36),
    STATUSKODE("statuskode", " ", 8, 1, 37, 37),
    DOEDSDATO("datoDoed", DUMMY_DATO, 9, 8, 38, 45),
    SLEKTSNAVN("slektsnavn", WHITESPACE_50_STK, 10, 50, 46, 95),
    FORNAVN("fornavn", WHITESPACE_50_STK, 11, 50, 96, 145),
    MELLOMNAVN("mellomnavn", WHITESPACE_50_STK, 12, 50, 146, 195),
    SLEKTSNAVN_UGIFT_T("slektsnavnUgift", WHITESPACE_50_STK, 13, 50, 196, 245),
    FORKORTET_NAVN("forkortetNavn", WHITESPACE_25_STK, 14, 25, 246, 270),
    REGDATO_NAVN("regDatoNavn", DUMMY_DATO, 15, 8, 271, 278),
    FOEDEKOMMUNE_LAND("foedekommLand", "    ", 16, 4, 279, 282),
    FOEDESTED("foedested", "                    ", 17, 20, 283, 302),
    STATSBORGERSKAP("statsborgerskap", "000", 18, 3, 303, 305),
    STATSBORGERSKAP_REGDATO("regdatoStatsb", DUMMY_DATO, 19, 8, 306, 313),
    FAMILIENUMMER("familienummer", DUMMY_IDENT, 20, 11, 314, 324),
    REG_DATO_FAM_NR("regdatoFamnr", DUMMY_DATO, 21, 8, 325, 332),
    PERSONKODE("personkode", "0", 22, 1, 333, 333),
    SPES_REGTYPE("spesRegType", "0", 23, 1, 334, 334),
    DATO_SPES_REGTYPE("datoSpesRegType", DUMMY_DATO, 24, 8, 335, 342),
    SIVILSTAND("sivilstand", "0", 25, 1, 343, 343),
    REGDATO_SIVILSTAND("regdatoSivilstand", DUMMY_DATO, 26, 8, 344, 351),
    EKTEFELLE_PARTNER_FODSELSDATO("ektefellePartnerFoedselsdato", SIX_OES, 28, 6, 352, 357),
    EKTEFELLE_PARTNER_PERSONNUMMMER("ektefellePartnerPersonnr", FIVE_OES, 29, 5, 358, 362),
    EKTEFELLE_PARTNER_NAVN("ektefellePartnerNavn", WHITESPACE_50_STK, 30, 50, 363, 412),
    EKTEFELLER_PARTNER_STATSBORGERSKAP("ektefellePartnerStatsb", "000", 31, 3, 413, 415),
    REG_DATO_ADR("regdatoAdr", DUMMY_DATO, 33, 8, 416, 423),
    FLYTTEDATO_ADR("flyttedatoAdr", DUMMY_DATO, 34, 8, 424, 431),
    KOMMUNENUMMER("kommunenummer", "0000", 35, 4, 432, 435),
    GATEGAARD("gateGaard", FIVE_OES, 36, 5, 436, 440),
    HUSBRUK("husBruk", "0000", 37, 4, 441, 444, true),
    BOKSTAVFESTENR("bokstavFestenr", "0000", 38, 4, 445, 448),
    UNDERNR("undernr", "000", 39, 3, 449, 451),
    ADRESSENAVN("adressenavn", WHITESPACE_25_STK, 40, 25, 452, 476),
    ADRESSETYPE("adressetype", " ", 41, 1, 477, 477),
    TILLEGGSADRESSE("tilleggsadresse", WHITESPACE_25_STK, 42, 25, 478, 502),
    POSTNUMMER("postnummer", "0000", 43, 4, 503, 506),
    VALGKRETS("valgkrets", "0000", 44, 4, 507, 510),
    POSTADRESSE1("postadresse1", WHITESPACE_30_STK, 46, 30, 511, 540),
    POSTADRESSE2("postadresse2", WHITESPACE_30_STK, 47, 30, 541, 570),
    POSTADRESSE3("postadresse3", WHITESPACE_30_STK, 48, 30, 571, 600),
    POSTADRESSE_LAND("postadresseLand", "000", 49, 3, 601, 603),
    INNVANDRET_FRA_LAND("innvandretFraLand", "000", 50, 3, 604, 606),
    FRA_LAND_REGDATO("fraLandRegdato", DUMMY_DATO, 51, 8, 607, 614),
    FRA_LAND_FLYTTEDATO("fraLandFlyttedato", DUMMY_DATO, 52, 8, 615, 622),
    FRA_KOMMUNE("fraKommune", "0000", 53, 4, 623, 626),
    FRA_KOMMUNE_REGDATO("fraKommRegdato", DUMMY_DATO, 54, 8, 627, 634),
    FRA_KOMMUNE_FLYTTEDATO("fraKommFlyttedato", DUMMY_DATO, 55, 8, 635, 642),
    UTVANDRET_TIL_LAND("utvandretTilLand", "000", 56, 3, 643, 645),
    TIL_LAND_REGDATO("tilLandRegdato", DUMMY_DATO, 57, 8, 646, 653),
    TIL_LAND_FLYTTEDATO("tilLandFlyttedato", DUMMY_DATO, 58, 8, 654, 661),
    SAMEMANNTALL("samemanntall", " ", 59, 1, 662, 662),
    DATO_SAMEMANNTALL("datoSamemanntall", DUMMY_DATO, 60, 8, 663, 670),
    UMYNDIGGJORT("umyndiggjort", " ", 61, 1, 671, 671),
    DATO_UMYNDIGGJORT("datoUmyndiggjort", DUMMY_DATO, 62, 8, 672, 679),
    FORELDREANSVAR("foreldreansvar", " ", 63, 1, 680, 680),
    DATO_FORELDREANSVAR("datoForeldreansvar", DUMMY_DATO, 64, 8, 681, 688),
    ARBEIDSTILLATELSE("arbeidstillatelse", " ", 65, 1, 689, 689),
    DATO_ARBEIDSTILLATELSE("datoArbeidstillatelse", DUMMY_DATO, 66, 8, 690, 697),
    FREMKONNUMMER("fremkonnummer", DUMMY_DATO, 67, 8, 698, 705),
    MORS_FODSELSDATO("morsFodselsdato", SIX_OES, 69, 6, 706, 711),
    MORS_PERSONNUMMER("morsPersonnummer", FIVE_OES, 70, 5, 712, 716),
    MORS_NAVN("morsNavn", WHITESPACE_50_STK, 71, 50, 717, 766),
    MORS_STATSBORGERSKAP("morsStatsbSkap", "000", 72, 3, 767, 769),
    FARS_FODSELSDATO("farsFodselsdato", SIX_OES, 74, 6, 770, 775),
    FARS_PERSONNUMMER("farsPersonnummer", FIVE_OES, 75, 5, 776, 780),
    FARS_NAVN("farsNavn", WHITESPACE_50_STK, 76, 50, 781, 830),
    FARS_STATSBSKAP("farsStatsbSkap", "000", 77, 3, 831, 833),
    TIDLIGERE_FNR_DNR("tidligereFnrDnr", DUMMY_IDENT, 78, 11, 834, 844),
    DATO_TIDLIGERE_FNRDNR("datoTidlFnrDnr", DUMMY_DATO, 79, 8, 845, 852),
    NYTT_FNR("nyttFnr", DUMMY_IDENT, 80, 11, 853, 863),
    DATO_NYTT_FNR("datoNyttFnr", DUMMY_DATO, 81, 8, 864, 871),
    LEVENDE_DOED("levendeDoed", " ", 82, 1, 872, 872),
    KJOENN("kjoenn", " ", 83, 1, 873, 873),
    TILDELINGSKODE("tildelingskode", " ", 84, 1, 874, 874),
    FOEDSELSTYPE("foedselstype", "  ", 85, 2, 875, 876),
    MORS_SIVILSTAND("morsSivilstand", " ", 86, 1, 877, 877),
    EKTESKAP_PARTNERSKAP_NUMMER("ekteskapPartnerskapNr", " ", 87, 1, 878, 878),
    EKTEFELLE_EKTESKAP_PARTNERSKAP_NUMMER("ektefelleEkteskapPartnerskapNr", " ", 88, 1, 879, 879),
    VIGSELSTYPE("vigselstype", " ", 89, 1, 880, 880),
    FORS_BYRDE("forsByrde", "  ", 90, 2, 881, 882),
    DOMBEVILLING("dombevilling", " ", 91, 1, 883, 883),
    ANTALL_BARN("antallBarn", "  ", 92, 2, 884, 885),
    TIDLIGERE_SIVILSTAND("tidligereSivilstand", " ", 93, 1, 886, 886),
    EKTEFELLE_TIDLIGERE_SIVILSTAND("ektefelleTidligereSivilstand", " ", 94, 1, 887, 887),
    HJEMMEL("hjemmel", " ", 95, 1, 888, 888),
    FYLKE("fylke", "  ", 96, 2, 889, 890),
    VIGSELSKOMMUNE("vigselskommune", "0000", 97, 4, 891, 894),
    TIDL_SEP_DOM_BEV("tidlSepDomBev", " ", 98, 1, 895, 895),
    BEGJERT_AV("begjertAv", " ", 99, 1, 896, 896),
    REGISTRERINGSGRUNNLAG("registrGrunnlag", " ", 100, 1, 897, 897),
    DOEDSSTED("doedssted", "    ", 101, 4, 898, 901),
    TYPE_DOEDSSTED("typeDoedssted", " ", 102, 1, 902, 902),
    VIGSELSDATO("vigselsdato", DUMMY_DATO, 103, 8, 903, 910),
    MEDLEM_KIRKEN("medlKirken", " ", 104, 1, 911, 911),
    SEKVENS_NR("sekvensnr", SIX_OES, 105, 6, 912, 917),
    BOLIG_NR("bolignr", "     ", 106, 5, 918, 922),
    DUF_ID("dufId", "000000000000", 107, 12, 923, 934),
    BRUKERIDENT("brukerident", "        ", 108, 8, 935, 942),
    SKOLERETS("skolerets", "0000", 109, 4, 943, 946),
    TK_NR("tkNr", "0000", 110, 4, 947, 950),
    DNR_HJEMLANDSADRESSE1("dnrHjemlandsadresse1", WHITESPACE_40_STK, 111, 40, 951, 990),
    DNR_HJEMLANDSADRESSE2("dnrHjemlandsadresse2", WHITESPACE_40_STK, 112, 40, 991, 1030),
    DNR_HJEMLANDSADRESSE3("dnrHjemlandsadresse3", WHITESPACE_40_STK, 113, 40, 1031, 1070),
    DNR_HJEMLAND_LANDKODE("dnrHjemlandLandkode", "000", 114, 3, 1071, 1073),
    DNR_HJEMLAND_REGDATO("dnrHjemlandRegDato", DUMMY_DATO, 115, 8, 1074, 1081),
    DNR_ID_KONTROLL("dnrIdKontroll", " ", 116, 1, 1082, 1082),
    POSTADRESSE_REGDATO("postadrRegDato", DUMMY_DATO, 117, 8, 1083, 1090),
    UTVANDRINGSTYPE("utvandringstype", " ", 118, 1, 1091, 1091),
    GRUNNKRETS("grunnkrets", "0000", 119, 4, 1092, 1095),
    STATSBORGERSKAP2("statsborgerskap2", "000", 120, 3, 1096, 1098),
    REGDATO_STATSB2("regdatoStatsb2", DUMMY_DATO, 121, 8, 1099, 1106),
    STATSBORGERSKAP3("statsborgerskap3", "000", 122, 3, 1107, 1109),
    REGDATO_STATSB3("regdatoStatsb3", DUMMY_DATO, 123, 8, 1110, 1117),
    STATSBORGERSKAP4("statsborgerskap4", "000", 124, 3, 1118, 1120),
    REGDATO_STATSB4("regdatoStatsb4", DUMMY_DATO, 125, 8, 1121, 1128),
    STATSBORGERSKAP5("statsborgerskap5", "000", 126, 3, 1129, 1131),
    REGDATO_STATSB5("regdatoStatsb5", DUMMY_DATO, 127, 8, 1132, 1139),
    STATSBORGERSKAP6("statsborgerskap6", "000", 128, 3, 1140, 1142),
    REGDATO_STATSB6("regdatoStatsb6", DUMMY_DATO, 129, 8, 1143, 1150),
    STATSBORGERSKAP7("statsborgerskap7", "000", 130, 3, 1151, 1153),
    REGDATO_STATSB7("regdatoStatsb7", DUMMY_DATO, 131, 8, 1154, 1161),
    STATSBORGERSKAP8("statsborgerskap8", "000", 132, 3, 1162, 1164),
    REGDATO_STATSB8("regdatoStatsb8", DUMMY_DATO, 132, 8, 1165, 1172),
    STATSBORGERSKAP9("statsborgerskap9", "000", 133, 3, 1173, 1175),
    REGDATO_STATSB9("regdatoStatsb9", DUMMY_DATO, 134, 8, 1176, 1183),
    STATSBORGERSKAP10("statsborgerskap10", "000", 135, 3, 1184, 1186),
    REGDATO_STATSB10("regdatoStatsb10", DUMMY_DATO, 136, 8, 1187, 1194),
    BIBEHOLD("bibehold", " ", 137, 1, 1195, 1195),
    REGDATO_BIBEHOLD("regdatoBibehold", DUMMY_DATO, 138, 8, 1196, 1203),
    SAKSID("saksid", "0000000", 139, 7, 1204, 1210),
    EMBETE("embete", "    ", 140, 4, 1211, 1214),
    SAKSTYPE("sakstype", "   ", 141, 3, 1215, 1217),
    VEDTAKSDATO("vedtaksdato", "        ", 142, 8, 1218, 1225),
    INTERN_VERGE_ID("internVergeid", "0000000", 143, 7, 1226, 1232),
    VERGE_FNR_DNR("vergeFnrDnr", DUMMY_IDENT, 144, 11, 1233, 1243),
    VERGETYPE("vergetype", "   ", 145, 3, 1244, 1246),
    MANDATTYPE("mandattype", "   ", 146, 3, 1247, 1249),
    MANDAT_TEKST("mandatTekst", WHITESPACE_100_STK, 147, 100, 1250, 1349),
    RESERVER_FRAMTIDIG_BRUK("reserverFramtidigBruk", WHITESPACE_100_STK + WHITESPACE_50_STK + " ", 148, 151, 1350, 1500);

    private String variabelNavn;
    private String defaultVerdi;
    private int idRekkefolge;
    private int antallBytesAvsatt;
    private int fraByte;
    private int tilByte;
    private boolean isValueLastInSkdField = false;

    public static final int TRANSTYPE_START_POSITION = 25;
    public static final int TRANSTYPE_END_POSITION = 26;

    SkdFeltDefinisjonerTrans1(String variabelnavn, String defaultVerdi, int idRekkefolge, int antallBytesAvsatt, int fraByte, int tilByte, boolean isValueLastInSkdField) {
        this.variabelNavn = variabelnavn;
        this.defaultVerdi = defaultVerdi;
        this.idRekkefolge = idRekkefolge;
        this.antallBytesAvsatt = antallBytesAvsatt;
        this.fraByte = fraByte;
        this.tilByte = tilByte;
        this.isValueLastInSkdField = isValueLastInSkdField;
    }

    SkdFeltDefinisjonerTrans1(String variabelnavn, String defaultVerdi, int idRekkefolge, int antallBytesAvsatt,
            int fraByte, int tilByte) {
        this.variabelNavn = variabelnavn;
        this.defaultVerdi = defaultVerdi;
        this.idRekkefolge = idRekkefolge;
        this.antallBytesAvsatt = antallBytesAvsatt;
        this.fraByte = fraByte;
        this.tilByte = tilByte;
    }

    public static List<SkdFeltDefinisjonerTrans1> getAllFeltDefinisjonerInSortedList() {
        return new ArrayList<>(Arrays.stream(SkdFeltDefinisjonerTrans1.values())
                .sorted(Comparator.comparingInt(SkdFeltDefinisjonerTrans1::getIdRekkefolge))
                .toList());
    }

    public String extractMeldingsfeltverdiFromString(String skdMeldingUtenHeader) {
        String extractedValue = skdMeldingUtenHeader.substring(this.getFraByte() - 1, this.getTilByte());
        return extractedValue.equals(this.getDefaultVerdi()) ? null : extractedValue.trim();
    }
}
