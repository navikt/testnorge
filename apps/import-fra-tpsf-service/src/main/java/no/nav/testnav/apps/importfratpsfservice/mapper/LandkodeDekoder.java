package no.nav.testnav.apps.importfratpsfservice.mapper;

import static java.time.LocalDateTime.of;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@UtilityClass
public class LandkodeDekoder {

    private static final String DEFAULT = "ZZZ";
    private static final Map<String, String> landkoderMap = new HashMap<>();

    static { //NOSONAR
        landkoderMap.put("???", DEFAULT);
        landkoderMap.put("657", "ABW");
        landkoderMap.put("404", "AFG");
        landkoderMap.put("204", "AGO");
        landkoderMap.put("660", "AIA");
        landkoderMap.put("111", "ALB");
        landkoderMap.put("114", "AND");
        landkoderMap.put("656", "ANT");
        landkoderMap.put("426", "ARE");
        landkoderMap.put("705", "ARG");
        landkoderMap.put("406", "ARM");
        landkoderMap.put("802", "ASM");
        landkoderMap.put("628", "ATF");
        landkoderMap.put("603", "ATG");
        landkoderMap.put("805", "AUS");
        landkoderMap.put("153", "AUT");
        landkoderMap.put("407", "AZE");
        landkoderMap.put("216", "BDI");
        landkoderMap.put("112", "BEL");
        landkoderMap.put("229", "BEN");
        landkoderMap.put("659", "BES");
        landkoderMap.put("393", "BFA");
        landkoderMap.put("410", "BGD");
        landkoderMap.put("113", "BGR");
        landkoderMap.put("409", "BHR");
        landkoderMap.put("605", "BHS");
        landkoderMap.put("155", "BIH");
        landkoderMap.put("687", "BLM");
        landkoderMap.put("120", "BLR");
        landkoderMap.put("604", "BLZ");
        landkoderMap.put("606", "BMU");
        landkoderMap.put("710", "BOL");
        landkoderMap.put("715", "BRA");
        landkoderMap.put("602", "BRB");
        landkoderMap.put("416", "BRN");
        landkoderMap.put("412", "BTN");
        landkoderMap.put("875", "BVT");
        landkoderMap.put("205", "BWA");
        landkoderMap.put("337", "CAF");
        landkoderMap.put("612", "CAN");
        landkoderMap.put("808", "CCK");
        landkoderMap.put("141", "CHE");
        landkoderMap.put("725", "CHL");
        landkoderMap.put("484", "CHN");
        landkoderMap.put("239", "CIV");
        landkoderMap.put("270", "CMR");
        landkoderMap.put("279", "COD");
        landkoderMap.put("278", "COG");
        landkoderMap.put("809", "COK");
        landkoderMap.put("730", "COL");
        landkoderMap.put("220", "COM");
        landkoderMap.put("273", "CPV");
        landkoderMap.put("616", "CRI");
        landkoderMap.put("142", "CSK");
        landkoderMap.put("620", "CUB");
        landkoderMap.put("661", "CUW");
        landkoderMap.put("807", "CXR");
        landkoderMap.put("613", "CYM");
        landkoderMap.put("500", "CYP");
        landkoderMap.put("158", "CZE");
        landkoderMap.put("151", "DDR");
        landkoderMap.put("144", "DEU");
        landkoderMap.put("250", "DJI");
        landkoderMap.put("622", "DMA");
        landkoderMap.put("101", "DNK");
        landkoderMap.put("624", "DOM");
        landkoderMap.put("203", "DZA");
        landkoderMap.put("735", "ECU");
        landkoderMap.put("249", "EGY");
        landkoderMap.put("241", "ERI");
        landkoderMap.put("304", "ESH");
        landkoderMap.put("137", "ESP");
        landkoderMap.put("115", "EST");
        landkoderMap.put("246", "ETH");
        landkoderMap.put("103", "FIN");
        landkoderMap.put("811", "FJI");
        landkoderMap.put("740", "FLK");
        landkoderMap.put("117", "FRA");
        landkoderMap.put("104", "FRO");
        landkoderMap.put("826", "FSM");
        landkoderMap.put("254", "GAB");
        landkoderMap.put("139", "GBR");
        landkoderMap.put("430", "GEO");
        landkoderMap.put("162", "GGY");
        landkoderMap.put("260", "GHA");
        landkoderMap.put("118", "GIB");
        landkoderMap.put("264", "GIN");
        landkoderMap.put("631", "GLP");
        landkoderMap.put("256", "GMB");
        landkoderMap.put("266", "GNB");
        landkoderMap.put("235", "GNQ");
        landkoderMap.put("119", "GRC");
        landkoderMap.put("629", "GRD");
        landkoderMap.put("102", "GRL");
        landkoderMap.put("632", "GTM");
        landkoderMap.put("745", "GUF");
        landkoderMap.put("817", "GUM");
        landkoderMap.put("720", "GUY");
        landkoderMap.put("436", "HKG");
        landkoderMap.put("870", "HMD");
        landkoderMap.put("644", "HND");
        landkoderMap.put("122", "HRV");
        landkoderMap.put("636", "HTI");
        landkoderMap.put("152", "HUN");
        landkoderMap.put("448", "IDN");
        landkoderMap.put("164", "IMN");
        landkoderMap.put("444", "IND");
        landkoderMap.put("213", "IOT");
        landkoderMap.put("121", "IRL");
        landkoderMap.put("456", "IRN");
        landkoderMap.put("452", "IRQ");
        landkoderMap.put("105", "ISL");
        landkoderMap.put("460", "ISR");
        landkoderMap.put("123", "ITA");
        landkoderMap.put("648", "JAM");
        landkoderMap.put("163", "JEY");
        landkoderMap.put("476", "JOR");
        landkoderMap.put("464", "JPN");
        landkoderMap.put("480", "KAZ");
        landkoderMap.put("276", "KEN");
        landkoderMap.put("502", "KGZ");
        landkoderMap.put("478", "KHM");
        landkoderMap.put("815", "KIR");
        landkoderMap.put("677", "KNA");
        landkoderMap.put("492", "KOR");
        landkoderMap.put("496", "KWT");
        landkoderMap.put("504", "LAO");
        landkoderMap.put("508", "LBN");
        landkoderMap.put("283", "LBR");
        landkoderMap.put("286", "LBY");
        landkoderMap.put("678", "LCA");
        landkoderMap.put("128", "LIE");
        landkoderMap.put("424", "LKA");
        landkoderMap.put("281", "LSO");
        landkoderMap.put("136", "LTU");
        landkoderMap.put("129", "LUX");
        landkoderMap.put("124", "LVA");
        landkoderMap.put("510", "MAC");
        landkoderMap.put("686", "MAF");
        landkoderMap.put("303", "MAR");
        landkoderMap.put("130", "MCO");
        landkoderMap.put("138", "MDA");
        landkoderMap.put("289", "MDG");
        landkoderMap.put("513", "MDV");
        landkoderMap.put("652", "MEX");
        landkoderMap.put("835", "MHL");
        landkoderMap.put("156", "MKD");
        landkoderMap.put("299", "MLI");
        landkoderMap.put("126", "MLT");
        landkoderMap.put("420", "MMR");
        landkoderMap.put("160", "MNE");
        landkoderMap.put("516", "MNG");
        landkoderMap.put("840", "MNP");
        landkoderMap.put("319", "MOZ");
        landkoderMap.put("306", "MRT");
        landkoderMap.put("654", "MSR");
        landkoderMap.put("650", "MTQ");
        landkoderMap.put("307", "MUS");
        landkoderMap.put("296", "MWI");
        landkoderMap.put("512", "MYS");
        landkoderMap.put("322", "MYT");
        landkoderMap.put("308", "NAM");
        landkoderMap.put("833", "NCL");
        landkoderMap.put("309", "NER");
        landkoderMap.put("822", "NFK");
        landkoderMap.put("313", "NGA");
        landkoderMap.put("664", "NIC");
        landkoderMap.put("821", "NIU");
        landkoderMap.put("127", "NLD");
        landkoderMap.put("000", "NOR");
        landkoderMap.put("528", "NPL");
        landkoderMap.put("818", "NRU");
        landkoderMap.put("820", "NZL");
        landkoderMap.put("520", "OMN");
        landkoderMap.put("534", "PAK");
        landkoderMap.put("668", "PAN");
        landkoderMap.put("828", "PCN");
        landkoderMap.put("760", "PER");
        landkoderMap.put("428", "PHL");
        landkoderMap.put("839", "PLW");
        landkoderMap.put("827", "PNG");
        landkoderMap.put("131", "POL");
        landkoderMap.put("685", "PRI");
        landkoderMap.put("488", "PRK");
        landkoderMap.put("132", "PRT");
        landkoderMap.put("755", "PRY");
        landkoderMap.put("524", "PSE");
        landkoderMap.put("814", "PYF");
        landkoderMap.put("540", "QAT");
        landkoderMap.put("323", "REU");
        landkoderMap.put("133", "ROU");
        landkoderMap.put("140", "RUS");
        landkoderMap.put("329", "RWA");
        landkoderMap.put("544", "SAU");
        landkoderMap.put("125", "SCG");
        landkoderMap.put("356", "SDN");
        landkoderMap.put("336", "SEN");
        landkoderMap.put("548", "SGP");
        landkoderMap.put("865", "SGS");
        landkoderMap.put("209", "SHN");
        landkoderMap.put("744", "SJM");
        landkoderMap.put("806", "SLB");
        landkoderMap.put("339", "SLE");
        landkoderMap.put("672", "SLV");
        landkoderMap.put("134", "SMR");
        landkoderMap.put("346", "SOM");
        landkoderMap.put("676", "SPM");
        landkoderMap.put("159", "SRB");
        landkoderMap.put("355", "SSD");
        landkoderMap.put("333", "STP");
        landkoderMap.put("135", "SUN");
        landkoderMap.put("765", "SUR");
        landkoderMap.put("157", "SVK");
        landkoderMap.put("146", "SVN");
        landkoderMap.put("106", "SWE");
        landkoderMap.put("357", "SWZ");
        landkoderMap.put("658", "SXM");
        landkoderMap.put("338", "SYC");
        landkoderMap.put("564", "SYR");
        landkoderMap.put("681", "TCA");
        landkoderMap.put("373", "TCD");
        landkoderMap.put("376", "TGO");
        landkoderMap.put("568", "THA");
        landkoderMap.put("550", "TJK");
        landkoderMap.put("829", "TKL");
        landkoderMap.put("552", "TKM");
        landkoderMap.put("537", "TLS");
        landkoderMap.put("813", "TON");
        landkoderMap.put("680", "TTO");
        landkoderMap.put("379", "TUN");
        landkoderMap.put("143", "TUR");
        landkoderMap.put("816", "TUV");
        landkoderMap.put("432", "TWN");
        landkoderMap.put("369", "TZA");
        landkoderMap.put("386", "UGA");
        landkoderMap.put("148", "UKR");
        landkoderMap.put("819", "UMI");
        landkoderMap.put("770", "URY");
        landkoderMap.put("684", "USA");
        landkoderMap.put("554", "UZB");
        landkoderMap.put("154", "VAT");
        landkoderMap.put("679", "VCT");
        landkoderMap.put("775", "VEN");
        landkoderMap.put("608", "VGB");
        landkoderMap.put("601", "VIR");
        landkoderMap.put("575", "VNM");
        landkoderMap.put("812", "VUT");
        landkoderMap.put("831", "WAK");
        landkoderMap.put("832", "WLF");
        landkoderMap.put("830", "WSM");
        landkoderMap.put("161", "XXK");
        landkoderMap.put("980", "XXX");
        landkoderMap.put("578", "YEM");
        landkoderMap.put("925", "YUG");
        landkoderMap.put("359", "ZAF");
        landkoderMap.put("389", "ZMB");
        landkoderMap.put("326", "ZWE");

//    public String getRandomLandTla() {
//
//        String landkode;
//        do {
//            landkode = (String) landkoderMap.keySet().toArray()[1 + random.nextInt(landkoderMap.size() - 2)];
//
//        } while (landkoderMap.get(landkode).fom.isAfter(START_OF_ERA) ||
//                landkoderMap.get(landkode).tom.isBefore(FORESEEABLE_FUTURE) ||
//                "NOR".equals(landkode));
//
//        return landkode;
//    }
//
//    public String encode(String statsborgerskap) {
//        return landkoderMap.getOrDefault(statsborgerskap, DEFAULT).getTpsCode();
//    }

//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    static class LandDetails {
//        private String tpsCode;
//        private LocalDateTime fom;
//        private LocalDateTime tom;
//    }
    }
}
