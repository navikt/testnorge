package no.nav.dolly.bestilling.tpsmessagingservice;


import java.util.HashMap;
import java.util.Map;

public enum KontoregisterLandkode {
    AD("Andorra", true, 24),
    BH("Bahrain", true, 22),
    BE("Belgia", true, 16),
    BG("Bulgaria", true, 22),
    DK("Danmark", true, 18),
    EE("Estland", true, 20),
    FI("Finland", true, 18),
    AE("Forente Arabiske Emirater", true, 23),
    FR("Frankrike", true, 27),
    FO("Færøyene", true, 18),
    GE("Georgia", true, 22),
    GI("Gibraltar", true, 23),
    GL("Grønland", true, 18),
    GR("Hellas", true, 27),
    IE("Irland", true, 22),
    IS("Island", true, 26),
    IM("Isle of Man", true, 22),
    IT("Italia", true, 27),
    IL("Israel", true, 23),
    JE("Jersey", true, 22),
    JO("Jordan", true, 30),
    HR("Kroatia", true, 21),
    CY("Kypros", true, 28),
    LV("Latvia", true, 21),
    LB("Libanon", true, 28),
    LI("Liechtenstein", true, 21),
    LT("Litauen", true, 20),
    LU("Luxembourg", true, 20),
    MK("Makedonia", true, 19),
    MT("Malta", true, 31),
    MD("Moldova", true, 24),
    MC("Monaco", true, 27),
    ME("Montenegro", true, 22),
    NL("Nederland", true, 18),
    NO("Norge", true, 15),
    PK("Pakistan", true, 24),
    PS("Palestina", true, 29),
    PL("Polen", true, 28),
    PT("Portugal", true, 25),
    QA("Qatar", true, 29),
    RO("Romania", true, 24),
    SM("San Marino", true, 27),
    SA("Saudi Arabia", true, 24),
    SK("Slovakia", true, 24),
    SI("Slovenia", true, 19),
    ES("Spania", true, 24),
    GB("Storbritannia", true, 22),
    CH("Sveits", true, 21),
    SE("Sverige", true, 24),
    CZ("Tsjekkia", true, 24),
    TN("Tunisia", true, 24),
    TR("Tyrkia", true, 26),
    DE("Tyskland", true, 22),
    HU("Ungarn", true, 28),
    AT("Østerrike", true, 20),
    XK("Kosovo", true, 20),
    TL("Øst-Timor", true, 23),
    VA("Vatikanstaten", true, 22),
    UA("Ukraina", true, 29),
    SD("Sudan", true, 18),
    LY("Libya", true, 25),
    AL("Albania", true, 28),
    AZ("Aserbajdsjan", true, 28),
    BA("Bosnia-Hercegovina", true, 20),
    BR("Brasil", true, 29),
    CR("Costa Rica", true, 22),
    DO("Den Dominikanske Republikk", true, 28),
    GT("Guatemala", true, 28),
    KZ("Kasakhstan", true, 20),
    KW("Kuwait", true, 30),
    MR("Mauritania", true, 27),
    MU("Mauritius", true, 30),
    RS("Serbia", true, 22),
    VG("Jomfruøyene", true, 24),
    SC("Seychellene", true, 31),
    ST("São Tomé og Príncipe", true, 25),
    LC("Saint Lucia", true, 32),
    IQ("Irak", true, 23),
    BY("Hviterussland", true, 28),
    SV("El Salvador", true, 28),
    EG("Egypt", true, 29),

    // Land som krever bankkode
    US("USA", false, null, true, 9),

    // Land som kan ha bankkode
    AU("Australia", false, null, false, 6),
    NZ("New Zeland", false, null, false, 6),
    ZA("Sør-Afrika", false, null, false, 6),
    CA("Canada", false, null, false, 9),
    IN("India", false, null, false, 11),
    RU("Russland", false, null, false, 9),

    // Land der man bør angi IBAN
    TD("Tsjad", 27),
    TG("Togo", 28),
    SN("Senegal", 28),
    NE("Niger", 28),
    NI("Nicaragua", 32),
    MZ("Mosambik", 25),
    MA("Marokko", 28),
    ML("Mali", 28),
    MG("Madagaskar", 27),
    KM("Komorene", 27),
    CV("Kapp Verde", 25),
    CM("Kamerun", 27),
    IR("Iran", 26),
    HN("Honduras", 28),
    GW("Guinea-Bissau", 25),
    GA("Gabon", 27),
    CI("Elfenbenskysten", 28),
    GQ("Ekvatorial-Guinea", 27),
    DJ("Djibouti", 27),
    CF("Den sentralafrikanske republikk", 27),
    CD("Kongo, Den demokratiske republikken", 27),
    BI("Burundi", 28),
    BF("Burkina Faso", 28),
    BJ("Benin", 28),
    AO("Angola", 25),
    DZ("Algerie", 26),

    // Andre land
    AX("Åland"),
    ZW("Zimbabwe"),
    ZM("Zambia"),
    WF("Wallis og Futuna"),
    VN("Vietnam"),
    EH("Vest-Sahara"),
    VE("Venezuela"),
    VU("Vanuatu"),
    UZ("Usbekistan"),
    UM("USAs ytre småøyer"),
    UY("Uruguay"),
    UG("Uganda"),
    TV("Tuvalu"),
    TC("Turks- og Caicosøyene"),
    TM("Turkmenistan"),
    TT("Trinidad og Tobago"),
    TO("Tonga"),
    TK("Tokelau"),
    TH("Thailand"),
    TZ("Tanzania"),
    TW("Taiwan"),
    TJ("Tadsjikistan"),
    SS("Sør-Sudan"),
    KR("Sør-Korea"),
    GS("Sør-Georgia og Sør-Sandwichøyene"),
    SY("Syria"),
    SZ("Swaziland"),
    SJ("Svalbard og Jan Mayen"),
    SR("Surinam"),
    SH("St. Helena, Ascension og Tristan da Cunha"),
    LK("Sri Lanka"),
    SO("Somalia"),
    SX("Sint Maarten"),
    SG("Singapore"),
    SL("Sierra Leone"),
    WS("Samoa"),
    SB("Salomonøyene"),
    PM("Saint-Pierre og Miquelon"),
    MF("Saint-Martin"),
    BL("Saint-Barthélemy"),
    VC("Saint Vincent og Grenadinene"),
    KN("Saint Kitts og Nevis"),
    RW("Rwanda"),
    RE("Réunion"),
    CG("Kongo, Republikken"),
    PR("Puerto Rico"),
    PN("Pitcairnøyene"),
    PE("Peru"),
    PY("Paraguay"),
    PG("Papua Ny-Guinea"),
    PA("Panama"),
    PW("Palau"),
    OM("Oman"),
    NC("Ny-Caledonia"),
    NF("Norfolkøya"),
    MP("Nord-Marianene"),
    KP("Nord-Korea"),
    NU("Niue"),
    NG("Nigeria"),
    NP("Nepal"),
    NR("Nauru"),
    NA("Namibia"),
    MM("Myanmar"),
    MS("Montserrat"),
    MN("Mongolia"),
    FM("Mikronesiaføderasjonen"),
    MX("Mexico"),
    YT("Mayotte"),
    MQ("Martinique"),
    MH("Marshalløyene"),
    MV("Maldivene"),
    MY("Malaysia"),
    MW("Malawi"),
    MO("Macao"),
    LR("Liberia"),
    LS("Lesotho"),
    LA("Laos"),
    CC("Kokosøyene"),
    KI("Kiribati"),
    KG("Kirgisistan"),
    CN("Kina"),
    KE("Kenya"),
    BQ("Bonaire, Sint Eustatius og Saba"),
    KH("Kambodsja"),
    YE("Jemen"),
    JP("Japan"),
    JM("Jamaica"),
    ID("Indonesia"),
    HK("Hongkong"),
    HM("Heard- og McDonaldøyene"),
    HT("Haiti"),
    GY("Guyana"),
    GN("Guinea"),
    GG("Guernsey"),
    GU("Guam"),
    GP("Guadeloupe"),
    GD("Grenada"),
    GH("Ghana"),
    GM("Gambia"),
    PF("Fransk Polynesia"),
    GF("Fransk Guyana"),
    PH("Filippinene"),
    FJ("Fiji"),
    FK("Falklandsøyene"),
    ET("Etiopia"),
    ER("Eritrea"),
    EC("Ecuador"),
    DM("Dominica"),
    IO("Det britiske territoriet i Indiahavet"),
    TF("De franske sørterritorier"),
    VI("Jomfruøyene, De amerikanske"),
    CW("Curaçao"),
    CU("Cuba"),
    CK("Cookøyene"),
    CO("Colombia"),
    CX("Christmasøya"),
    CL("Chile"),
    KY("Caymanøyene"),
    BN("Brunei"),
    BV("Bouvetøya"),
    BW("Botswana"),
    BO("Bolivia"),
    BT("Bhutan"),
    BM("Bermuda"),
    BZ("Belize"),
    BB("Barbados"),
    BD("Bangladesh"),
    BS("Bahamas"),
    AW("Aruba"),
    AM("Armenia"),
    AR("Argentina"),
    AG("Antigua og Barbuda"),
    AQ("Antarktika"),
    AI("Anguilla"),
    AS("Amerikansk Samoa"),
    AF("Afghanistan");

    private final String land;
    private final boolean kreverIban;
    private final Integer ibanLengde;
    private final boolean kreverBankkode;
    private final Integer bankkodeLengde;

    KontoregisterLandkode(String land, boolean kreverIban, Integer ibanLengde, boolean kreverBankkode, Integer bankkodeLengde) {
        this.land = land;
        this.kreverIban = kreverIban;
        this.ibanLengde = ibanLengde;
        this.kreverBankkode = kreverBankkode;
        this.bankkodeLengde = bankkodeLengde;
    }

    KontoregisterLandkode(String land, Boolean kreverIban, Integer ibanLengde) {
        this(land, kreverIban, ibanLengde, false, null);
    }

    KontoregisterLandkode(String land, Integer ibanLengde) {
        this(land, false, ibanLengde, false, null);
    }

    KontoregisterLandkode(String land) {
        this(land, false, null, false, null);
    }

    public String getLand() {
        return land;
    }

    public Integer getIbanLengde() {
        return ibanLengde;
    }

    private static final Map<String, String> landkodeIsoMapping = new HashMap<>();

    public static String getIso2FromIso(String iso) {
        if (landkodeIsoMapping.containsKey(iso)) {
            return landkodeIsoMapping.get(iso);
        }
        return iso;
    }

    static {
        landkodeIsoMapping.put("AFG", "AF");
        landkodeIsoMapping.put("ALB", "AL");
        landkodeIsoMapping.put("DZA", "DZ");
        landkodeIsoMapping.put("ASM", "AS");
        landkodeIsoMapping.put("AND", "AD");
        landkodeIsoMapping.put("AGO", "AO");
        landkodeIsoMapping.put("AIA", "AI");
        landkodeIsoMapping.put("ATG", "AG");
        landkodeIsoMapping.put("ARG", "AR");
        landkodeIsoMapping.put("ARM", "AM");
        landkodeIsoMapping.put("ABW", "AW");
        landkodeIsoMapping.put("AZE", "AZ");
        landkodeIsoMapping.put("AUS", "AU");
        landkodeIsoMapping.put("BHS", "BS");
        landkodeIsoMapping.put("BHR", "BH");
        landkodeIsoMapping.put("BGD", "BD");
        landkodeIsoMapping.put("BRB", "BB");
        landkodeIsoMapping.put("BEL", "BE");
        landkodeIsoMapping.put("BLZ", "BZ");
        landkodeIsoMapping.put("BEN", "BJ");
        landkodeIsoMapping.put("BMU", "BM");
        landkodeIsoMapping.put("BTN", "BT");
        landkodeIsoMapping.put("BOL", "BO");
        landkodeIsoMapping.put("BES", "BQ");
        landkodeIsoMapping.put("BIH", "BA");
        landkodeIsoMapping.put("BWA", "BW");
        landkodeIsoMapping.put("BVT", "BV");
        landkodeIsoMapping.put("BRA", "BR");
        landkodeIsoMapping.put("BRN", "BN");
        landkodeIsoMapping.put("BGR", "BG");
        landkodeIsoMapping.put("BFA", "BF");
        landkodeIsoMapping.put("BDI", "BI");
        landkodeIsoMapping.put("CAN", "CA");
        landkodeIsoMapping.put("CYM", "KY");
        landkodeIsoMapping.put("CHL", "CL");
        landkodeIsoMapping.put("CXR", "CX");
        landkodeIsoMapping.put("COL", "CO");
        landkodeIsoMapping.put("COK", "CK");
        landkodeIsoMapping.put("CRI", "CR");
        landkodeIsoMapping.put("CUB", "CU");
        landkodeIsoMapping.put("CUW", "CW");
        landkodeIsoMapping.put("DNK", "DK");
        landkodeIsoMapping.put("VIR", "VI");
        landkodeIsoMapping.put("VGB", "VG");
        landkodeIsoMapping.put("ARE", "AE");
        landkodeIsoMapping.put("DOM", "DO");
        landkodeIsoMapping.put("CAF", "CF");
        landkodeIsoMapping.put("IOT", "IO");
        landkodeIsoMapping.put("DJI", "DJ");
        landkodeIsoMapping.put("DMA", "DM");
        landkodeIsoMapping.put("ECU", "EC");
        landkodeIsoMapping.put("EGY", "EG");
        landkodeIsoMapping.put("GNQ", "GQ");
        landkodeIsoMapping.put("SLV", "SV");
        landkodeIsoMapping.put("CIV", "CI");
        landkodeIsoMapping.put("ERI", "ER");
        landkodeIsoMapping.put("EST", "EE");
        landkodeIsoMapping.put("SWZ", "SZ");
        landkodeIsoMapping.put("ETH", "ET");
        landkodeIsoMapping.put("FLK", "FK");
        landkodeIsoMapping.put("FJI", "FJ");
        landkodeIsoMapping.put("PHL", "PH");
        landkodeIsoMapping.put("FIN", "FI");
        landkodeIsoMapping.put("FRA", "FR");
        landkodeIsoMapping.put("GUF", "GF");
        landkodeIsoMapping.put("PYF", "PF");
        landkodeIsoMapping.put("FRO", "FO");
        landkodeIsoMapping.put("GAB", "GA");
        landkodeIsoMapping.put("GMB", "GM");
        landkodeIsoMapping.put("GEO", "GE");
        landkodeIsoMapping.put("GHA", "GH");
        landkodeIsoMapping.put("GIB", "GI");
        landkodeIsoMapping.put("GRD", "GD");
        landkodeIsoMapping.put("GRL", "GL");
        landkodeIsoMapping.put("GLP", "GP");
        landkodeIsoMapping.put("GUM", "GU");
        landkodeIsoMapping.put("GTM", "GT");
        landkodeIsoMapping.put("GGY", "GG");
        landkodeIsoMapping.put("GIN", "GN");
        landkodeIsoMapping.put("GNB", "GW");
        landkodeIsoMapping.put("GUY", "GY");
        landkodeIsoMapping.put("HTI", "HT");
        landkodeIsoMapping.put("HMD", "HM");
        landkodeIsoMapping.put("GRC", "GR");
        landkodeIsoMapping.put("HND", "HN");
        landkodeIsoMapping.put("HKG", "HK");
        landkodeIsoMapping.put("BLR", "BY");
        landkodeIsoMapping.put("IND", "IN");
        landkodeIsoMapping.put("IDN", "ID");
        landkodeIsoMapping.put("IRQ", "IQ");
        landkodeIsoMapping.put("IRN", "IR");
        landkodeIsoMapping.put("IRL", "IE");
        landkodeIsoMapping.put("ISL", "IS");
        landkodeIsoMapping.put("IMN", "IM");
        landkodeIsoMapping.put("ISR", "IL");
        landkodeIsoMapping.put("ITA", "IT");
        landkodeIsoMapping.put("JAM", "JM");
        landkodeIsoMapping.put("JPN", "JP");
        landkodeIsoMapping.put("YEM", "YE");
        landkodeIsoMapping.put("JEY", "JE");
        landkodeIsoMapping.put("JOR", "JO");
        landkodeIsoMapping.put("KHM", "KH");
        landkodeIsoMapping.put("CMR", "CM");
        landkodeIsoMapping.put("CPV", "CV");
        landkodeIsoMapping.put("KAZ", "KZ");
        landkodeIsoMapping.put("KEN", "KE");
        landkodeIsoMapping.put("CHN", "CN");
        landkodeIsoMapping.put("KGZ", "KG");
        landkodeIsoMapping.put("KIR", "KI");
        landkodeIsoMapping.put("CCK", "CC");
        landkodeIsoMapping.put("COM", "KM");
        landkodeIsoMapping.put("COD", "CD");
        landkodeIsoMapping.put("COG", "CG");
        landkodeIsoMapping.put("XXK", "XK");
        landkodeIsoMapping.put("HRV", "HR");
        landkodeIsoMapping.put("KWT", "KW");
        landkodeIsoMapping.put("CYP", "CY");
        landkodeIsoMapping.put("LAO", "LA");
        landkodeIsoMapping.put("LVA", "LV");
        landkodeIsoMapping.put("LSO", "LS");
        landkodeIsoMapping.put("LBN", "LB");
        landkodeIsoMapping.put("LBR", "LR");
        landkodeIsoMapping.put("LBY", "LY");
        landkodeIsoMapping.put("LIE", "LI");
        landkodeIsoMapping.put("LTU", "LT");
        landkodeIsoMapping.put("LUX", "LU");
        landkodeIsoMapping.put("MAC", "MO");
        landkodeIsoMapping.put("MDG", "MG");
        landkodeIsoMapping.put("MWI", "MW");
        landkodeIsoMapping.put("MYS", "MY");
        landkodeIsoMapping.put("MDV", "MV");
        landkodeIsoMapping.put("MLI", "ML");
        landkodeIsoMapping.put("MLT", "MT");
        landkodeIsoMapping.put("MAR", "MA");
        landkodeIsoMapping.put("MHL", "MH");
        landkodeIsoMapping.put("MTQ", "MQ");
        landkodeIsoMapping.put("MRT", "MR");
        landkodeIsoMapping.put("MUS", "MU");
        landkodeIsoMapping.put("MYT", "YT");
        landkodeIsoMapping.put("MEX", "MX");
        landkodeIsoMapping.put("FSM", "FM");
        landkodeIsoMapping.put("MDA", "MD");
        landkodeIsoMapping.put("MCO", "MC");
        landkodeIsoMapping.put("MNG", "MN");
        landkodeIsoMapping.put("MNE", "ME");
        landkodeIsoMapping.put("MSR", "MS");
        landkodeIsoMapping.put("MOZ", "MZ");
        landkodeIsoMapping.put("MMR", "MM");
        landkodeIsoMapping.put("NAM", "NA");
        landkodeIsoMapping.put("NRU", "NR");
        landkodeIsoMapping.put("NLD", "NL");
        landkodeIsoMapping.put("NPL", "NP");
        landkodeIsoMapping.put("NZL", "NZ");
        landkodeIsoMapping.put("NIC", "NI");
        landkodeIsoMapping.put("NER", "NE");
        landkodeIsoMapping.put("NGA", "NG");
        landkodeIsoMapping.put("NIU", "NU");
        landkodeIsoMapping.put("PRK", "KP");
        landkodeIsoMapping.put("MKD", "MK");
        landkodeIsoMapping.put("MNP", "MP");
        landkodeIsoMapping.put("NFK", "NF");
        landkodeIsoMapping.put("NOR", "NO");
        landkodeIsoMapping.put("NCL", "NC");
        landkodeIsoMapping.put("OMN", "OM");
        landkodeIsoMapping.put("PAK", "PK");
        landkodeIsoMapping.put("PLW", "PW");
        landkodeIsoMapping.put("PSE", "PS");
        landkodeIsoMapping.put("PAN", "PA");
        landkodeIsoMapping.put("PNG", "PG");
        landkodeIsoMapping.put("PRY", "PY");
        landkodeIsoMapping.put("PER", "PE");
        landkodeIsoMapping.put("PCN", "PN");
        landkodeIsoMapping.put("POL", "PL");
        landkodeIsoMapping.put("PRT", "PT");
        landkodeIsoMapping.put("PRI", "PR");
        landkodeIsoMapping.put("QAT", "QA");
        landkodeIsoMapping.put("REU", "RE");
        landkodeIsoMapping.put("ROU", "RO");
        landkodeIsoMapping.put("RUS", "RU");
        landkodeIsoMapping.put("RWA", "RW");
        landkodeIsoMapping.put("BLM", "BL");
        landkodeIsoMapping.put("KNA", "KN");
        landkodeIsoMapping.put("LCA", "LC");
        landkodeIsoMapping.put("MAF", "MF");
        landkodeIsoMapping.put("VCT", "VC");
        landkodeIsoMapping.put("SPM", "PM");
        landkodeIsoMapping.put("SLB", "SB");
        landkodeIsoMapping.put("WSM", "WS");
        landkodeIsoMapping.put("SMR", "SM");
        landkodeIsoMapping.put("SHN", "SH");
        landkodeIsoMapping.put("STP", "ST");
        landkodeIsoMapping.put("SAU", "SA");
        landkodeIsoMapping.put("SEN", "SN");
        landkodeIsoMapping.put("SRB", "RS");
        landkodeIsoMapping.put("SCG", "CS");
        landkodeIsoMapping.put("SYC", "SC");
        landkodeIsoMapping.put("SLE", "SL");
        landkodeIsoMapping.put("SGP", "SG");
        landkodeIsoMapping.put("SXM", "SX");
        landkodeIsoMapping.put("SVK", "SK");
        landkodeIsoMapping.put("SVN", "SI");
        landkodeIsoMapping.put("SOM", "SO");
        landkodeIsoMapping.put("ESP", "ES");
        landkodeIsoMapping.put("LKA", "LK");
        landkodeIsoMapping.put("GBR", "GB");
        landkodeIsoMapping.put("SDN", "SD");
        landkodeIsoMapping.put("SUR", "SR");
        landkodeIsoMapping.put("SJM", "SJ");
        landkodeIsoMapping.put("CHE", "CH");
        landkodeIsoMapping.put("SWE", "SE");
        landkodeIsoMapping.put("SYR", "SY");
        landkodeIsoMapping.put("ZAF", "ZA");
        landkodeIsoMapping.put("KOR", "KR");
        landkodeIsoMapping.put("SSD", "SS");
        landkodeIsoMapping.put("TJK", "TJ");
        landkodeIsoMapping.put("TWN", "TW");
        landkodeIsoMapping.put("TZA", "TZ");
        landkodeIsoMapping.put("THA", "TH");
        landkodeIsoMapping.put("TGO", "TG");
        landkodeIsoMapping.put("TKL", "TK");
        landkodeIsoMapping.put("TON", "TO");
        landkodeIsoMapping.put("TTO", "TT");
        landkodeIsoMapping.put("TCD", "TD");
        landkodeIsoMapping.put("CZE", "CZ");
        landkodeIsoMapping.put("TUN", "TN");
        landkodeIsoMapping.put("TKM", "TM");
        landkodeIsoMapping.put("TCA", "TC");
        landkodeIsoMapping.put("TUV", "TV");
        landkodeIsoMapping.put("TUR", "TR");
        landkodeIsoMapping.put("DEU", "DE");
        landkodeIsoMapping.put("UGA", "UG");
        landkodeIsoMapping.put("UKR", "UA");
        landkodeIsoMapping.put("HUN", "HU");
        landkodeIsoMapping.put("URY", "UY");
        landkodeIsoMapping.put("USA", "US");
        landkodeIsoMapping.put("UMI", "UM");
        landkodeIsoMapping.put("UZB", "UZ");
        landkodeIsoMapping.put("VUT", "VU");
        landkodeIsoMapping.put("VAT", "VA");
        landkodeIsoMapping.put("VEN", "VE");
        landkodeIsoMapping.put("ESH", "EH");
        landkodeIsoMapping.put("VNM", "VN");
        landkodeIsoMapping.put("WLF", "WF");
        landkodeIsoMapping.put("ZMB", "ZM");
        landkodeIsoMapping.put("ZWE", "ZW");
        landkodeIsoMapping.put("ALA", "AX");
        landkodeIsoMapping.put("TLS", "TL");
        landkodeIsoMapping.put("AUT", "AT");
    }

}
