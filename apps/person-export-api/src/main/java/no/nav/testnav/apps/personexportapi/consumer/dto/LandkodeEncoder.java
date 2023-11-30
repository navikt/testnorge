package no.nav.testnav.apps.personexportapi.consumer.dto;

import lombok.experimental.UtilityClass;
import org.bouncycastle.asn1.eac.BidirectionalMap;

import static java.util.Objects.nonNull;

@UtilityClass
public class LandkodeEncoder {

    private static final BidirectionalMap landkoderMap = new BidirectionalMap();

    private static final String DEFAULT = "990";

    static { //NOSONAR
        landkoderMap.put("???", DEFAULT);
        landkoderMap.put("ABW", "657");
        landkoderMap.put("AFG", "404");
        landkoderMap.put("AGO", "204");
        landkoderMap.put("AIA", "660");
        landkoderMap.put("ALA", "860");
        landkoderMap.put("ALB", "111");
        landkoderMap.put("AND", "114");
        landkoderMap.put("ANT", "656");
        landkoderMap.put("ARE", "426");
        landkoderMap.put("ARG", "705");
        landkoderMap.put("ARM", "406");
        landkoderMap.put("ASM", "802");
        landkoderMap.put("ATF", "628");
        landkoderMap.put("ATG", "603");
        landkoderMap.put("AUS", "805");
        landkoderMap.put("AUT", "153");
        landkoderMap.put("AZE", "407");
        landkoderMap.put("BDI", "216");
        landkoderMap.put("BEL", "112");
        landkoderMap.put("BEN", "229");
        landkoderMap.put("BES", "659");
        landkoderMap.put("BFA", "393");
        landkoderMap.put("BGD", "410");
        landkoderMap.put("BGR", "113");
        landkoderMap.put("BHR", "409");
        landkoderMap.put("BHS", "605");
        landkoderMap.put("BIH", "155");
        landkoderMap.put("BLM", "687");
        landkoderMap.put("BLR", "120");
        landkoderMap.put("BLZ", "604");
        landkoderMap.put("BMU", "606");
        landkoderMap.put("BOL", "710");
        landkoderMap.put("BRA", "715");
        landkoderMap.put("BRB", "602");
        landkoderMap.put("BRN", "416");
        landkoderMap.put("BTN", "412");
        landkoderMap.put("BVT", "875");
        landkoderMap.put("BWA", "205");
        landkoderMap.put("CAF", "337");
        landkoderMap.put("CAN", "612");
        landkoderMap.put("CCK", "808");
        landkoderMap.put("CHE", "141");
        landkoderMap.put("CHL", "725");
        landkoderMap.put("CHN", "484");
        landkoderMap.put("CIV", "239");
        landkoderMap.put("CMR", "270");
        landkoderMap.put("COD", "279");
        landkoderMap.put("COG", "278");
        landkoderMap.put("COK", "809");
        landkoderMap.put("COL", "730");
        landkoderMap.put("COM", "220");
        landkoderMap.put("CPV", "273");
        landkoderMap.put("CRI", "616");
        landkoderMap.put("CSK", "142");
        landkoderMap.put("CUB", "620");
        landkoderMap.put("CUW", "661");
        landkoderMap.put("CXR", "807");
        landkoderMap.put("CYM", "613");
        landkoderMap.put("CYP", "500");
        landkoderMap.put("CZE", "158");
        landkoderMap.put("DDR", "151");
        landkoderMap.put("DEU", "144");
        landkoderMap.put("DJI", "250");
        landkoderMap.put("DMA", "622");
        landkoderMap.put("DNK", "101");
        landkoderMap.put("DOM", "624");
        landkoderMap.put("DZA", "203");
        landkoderMap.put("ECU", "735");
        landkoderMap.put("EGY", "249");
        landkoderMap.put("ERI", "241");
        landkoderMap.put("ESH", "304");
        landkoderMap.put("ESP", "137");
        landkoderMap.put("EST", "115");
        landkoderMap.put("ETH", "246");
        landkoderMap.put("FIN", "103");
        landkoderMap.put("FJI", "811");
        landkoderMap.put("FLK", "740");
        landkoderMap.put("FRA", "117");
        landkoderMap.put("FRO", "104");
        landkoderMap.put("FSM", "826");
        landkoderMap.put("GAB", "254");
        landkoderMap.put("GBR", "139");
        landkoderMap.put("GEO", "430");
        landkoderMap.put("GGY", "162");
        landkoderMap.put("GHA", "260");
        landkoderMap.put("GIB", "118");
        landkoderMap.put("GIN", "264");
        landkoderMap.put("GLP", "631");
        landkoderMap.put("GMB", "256");
        landkoderMap.put("GNB", "266");
        landkoderMap.put("GNQ", "235");
        landkoderMap.put("GRC", "119");
        landkoderMap.put("GRD", "629");
        landkoderMap.put("GRL", "102");
        landkoderMap.put("GTM", "632");
        landkoderMap.put("GUF", "745");
        landkoderMap.put("GUM", "817");
        landkoderMap.put("GUY", "720");
        landkoderMap.put("HKG", "436");
        landkoderMap.put("HMD", "870");
        landkoderMap.put("HND", "644");
        landkoderMap.put("HRV", "122");
        landkoderMap.put("HTI", "636");
        landkoderMap.put("HUN", "152");
        landkoderMap.put("IDN", "448");
        landkoderMap.put("IMN", "164");
        landkoderMap.put("IND", "444");
        landkoderMap.put("IOT", "213");
        landkoderMap.put("IRL", "121");
        landkoderMap.put("IRN", "456");
        landkoderMap.put("IRQ", "452");
        landkoderMap.put("ISL", "105");
        landkoderMap.put("ISR", "460");
        landkoderMap.put("ITA", "123");
        landkoderMap.put("JAM", "648");
        landkoderMap.put("JEY", "163");
        landkoderMap.put("JOR", "476");
        landkoderMap.put("JPN", "464");
        landkoderMap.put("KAZ", "480");
        landkoderMap.put("KEN", "276");
        landkoderMap.put("KGZ", "502");
        landkoderMap.put("KHM", "478");
        landkoderMap.put("KIR", "815");
        landkoderMap.put("KNA", "677");
        landkoderMap.put("KOR", "492");
        landkoderMap.put("KWT", "496");
        landkoderMap.put("LAO", "504");
        landkoderMap.put("LBN", "508");
        landkoderMap.put("LBR", "283");
        landkoderMap.put("LBY", "286");
        landkoderMap.put("LCA", "678");
        landkoderMap.put("LIE", "128");
        landkoderMap.put("LKA", "424");
        landkoderMap.put("LSO", "281");
        landkoderMap.put("LTU", "136");
        landkoderMap.put("LUX", "129");
        landkoderMap.put("LVA", "124");
        landkoderMap.put("MAC", "510");
        landkoderMap.put("MAF", "686");
        landkoderMap.put("MAR", "303");
        landkoderMap.put("MCO", "130");
        landkoderMap.put("MDA", "138");
        landkoderMap.put("MDG", "289");
        landkoderMap.put("MDV", "513");
        landkoderMap.put("MEX", "652");
        landkoderMap.put("MHL", "835");
        landkoderMap.put("MKD", "156");
        landkoderMap.put("MLI", "299");
        landkoderMap.put("MLT", "126");
        landkoderMap.put("MMR", "420");
        landkoderMap.put("MNE", "160");
        landkoderMap.put("MNG", "516");
        landkoderMap.put("MNP", "840");
        landkoderMap.put("MOZ", "319");
        landkoderMap.put("MRT", "306");
        landkoderMap.put("MSR", "654");
        landkoderMap.put("MTQ", "650");
        landkoderMap.put("MUS", "307");
        landkoderMap.put("MWI", "296");
        landkoderMap.put("MYS", "512");
        landkoderMap.put("MYT", "322");
        landkoderMap.put("NAM", "308");
        landkoderMap.put("NCL", "833");
        landkoderMap.put("NER", "309");
        landkoderMap.put("NFK", "822");
        landkoderMap.put("NGA", "313");
        landkoderMap.put("NIC", "664");
        landkoderMap.put("NIU", "821");
        landkoderMap.put("NLD", "127");
        landkoderMap.put("NOR", "000");
        landkoderMap.put("NPL", "528");
        landkoderMap.put("NRU", "818");
        landkoderMap.put("NZL", "820");
        landkoderMap.put("OMN", "520");
        landkoderMap.put("PAK", "534");
        landkoderMap.put("PAN", "668");
        landkoderMap.put("PCN", "828");
        landkoderMap.put("PER", "760");
        landkoderMap.put("PHL", "428");
        landkoderMap.put("PLW", "839");
        landkoderMap.put("PNG", "827");
        landkoderMap.put("POL", "131");
        landkoderMap.put("PRI", "685");
        landkoderMap.put("PRK", "488");
        landkoderMap.put("PRT", "132");
        landkoderMap.put("PRY", "755");
        landkoderMap.put("PSE", "524");
        landkoderMap.put("PYF", "814");
        landkoderMap.put("QAT", "540");
        landkoderMap.put("REU", "323");
        landkoderMap.put("ROU", "133");
        landkoderMap.put("RUS", "140");
        landkoderMap.put("RWA", "329");
        landkoderMap.put("SAU", "544");
        landkoderMap.put("SCG", "125");
        landkoderMap.put("SDN", "356");
        landkoderMap.put("SEN", "336");
        landkoderMap.put("SGP", "548");
        landkoderMap.put("SGS", "865");
        landkoderMap.put("SHN", "209");
        landkoderMap.put("SJM", "744");
        landkoderMap.put("SLB", "806");
        landkoderMap.put("SLE", "339");
        landkoderMap.put("SLV", "672");
        landkoderMap.put("SMR", "134");
        landkoderMap.put("SOM", "346");
        landkoderMap.put("SPM", "676");
        landkoderMap.put("SRB", "159");
        landkoderMap.put("SSD", "355");
        landkoderMap.put("STP", "333");
        landkoderMap.put("SUN", "135");
        landkoderMap.put("SUR", "765");
        landkoderMap.put("SVK", "157");
        landkoderMap.put("SVN", "146");
        landkoderMap.put("SWE", "106");
        landkoderMap.put("SWZ", "357");
        landkoderMap.put("SXM", "658");
        landkoderMap.put("SYC", "338");
        landkoderMap.put("SYR", "564");
        landkoderMap.put("TCA", "681");
        landkoderMap.put("TCD", "373");
        landkoderMap.put("TGO", "376");
        landkoderMap.put("THA", "568");
        landkoderMap.put("TJK", "550");
        landkoderMap.put("TKL", "829");
        landkoderMap.put("TKM", "552");
        landkoderMap.put("TLS", "537");
        landkoderMap.put("TON", "813");
        landkoderMap.put("TTO", "680");
        landkoderMap.put("TUN", "379");
        landkoderMap.put("TUR", "143");
        landkoderMap.put("TUV", "816");
        landkoderMap.put("TWN", "432");
        landkoderMap.put("TZA", "369");
        landkoderMap.put("UGA", "386");
        landkoderMap.put("UKR", "148");
        landkoderMap.put("UMI", "819");
        landkoderMap.put("URY", "770");
        landkoderMap.put("USA", "684");
        landkoderMap.put("UZB", "554");
        landkoderMap.put("VAT", "154");
        landkoderMap.put("VCT", "679");
        landkoderMap.put("VEN", "775");
        landkoderMap.put("VGB", "608");
        landkoderMap.put("VIR", "601");
        landkoderMap.put("VNM", "575");
        landkoderMap.put("VUT", "812");
        landkoderMap.put("WAK", "831");
        landkoderMap.put("WLF", "832");
        landkoderMap.put("WSM", "830");
        landkoderMap.put("XXK", "161");
        landkoderMap.put("XXX", "980");
        landkoderMap.put("YEM", "578");
        landkoderMap.put("YUG", "925");
        landkoderMap.put("ZAF", "359");
        landkoderMap.put("ZMB", "389");
        landkoderMap.put("ZWE", "326");
        landkoderMap.put("349", "349");
        landkoderMap.put("546", "546");
        landkoderMap.put("556", "556");
        landkoderMap.put("669", "669");
    }

    public static String encode(String statsborgerskap) {
        return (String) landkoderMap.getOrDefault(statsborgerskap, DEFAULT);
    }

    public static String decode(String statsborgerskap) {
        return nonNull(landkoderMap.getReverse(statsborgerskap)) ?
                (String) landkoderMap.getReverse(statsborgerskap) : "???";
    }
}
