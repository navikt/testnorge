package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

@UtilityClass
public class Kor2024KommuneEndringer {

    private static String HARAM = "1580";
    private static String AALESUND = "1508";
    private static Random postnummer = new SecureRandom();

    private static Map<String, String> KOMMUNER = new HashMap<>();

    static {
        KOMMUNER.put("1508", "1507");  // Ålesund
        KOMMUNER.put("1580", "1507");  // Haram
        KOMMUNER.put("3101", "3001");  // Halden
        KOMMUNER.put("3103", "3002");  // Moss
        KOMMUNER.put("3105", "3003");  // Sarpsborg
        KOMMUNER.put("3107", "3004");  // Fredrikstad
        KOMMUNER.put("3301", "3005");  // Drammen
        KOMMUNER.put("3303", "3006");  // Kongsberg
        KOMMUNER.put("3305", "3007");  // Ringerike
        KOMMUNER.put("3110", "3011");  // Hvaler
        KOMMUNER.put("3124", "3012");  // Aremark
        KOMMUNER.put("3122", "3013");  // Marker
        KOMMUNER.put("3118", "3014");  // Indre Østfold
        KOMMUNER.put("3116", "3015");  // Skiptvet
        KOMMUNER.put("3120", "3016");  // Rakkestad
        KOMMUNER.put("3112", "3017");  // Råde
        KOMMUNER.put("3114", "3018");  // Våler
        KOMMUNER.put("3216", "3019");  // Vestby
        KOMMUNER.put("3207", "3020");  // Nordre Follo
        KOMMUNER.put("3218", "3021");  // Ås
        KOMMUNER.put("3214", "3022");  // Frogn
        KOMMUNER.put("3212", "3023");  // Nesodden
        KOMMUNER.put("3201", "3024");  // Bærum
        KOMMUNER.put("3203", "3025");  // Asker
        KOMMUNER.put("3226", "3026");  // Aurskog-Høland
        KOMMUNER.put("3224", "3027");  // Rælingen
        KOMMUNER.put("3220", "3028");  // Enebakk
        KOMMUNER.put("3222", "3029");  // Lørenskog
        KOMMUNER.put("3205", "3030");  // Lillestrøm
        KOMMUNER.put("3232", "3031");  // Nittedal
        KOMMUNER.put("3230", "3032");  // Gjerdrum
        KOMMUNER.put("3209", "3033");  // Ullensaker
        KOMMUNER.put("3228", "3034");  // Nes
        KOMMUNER.put("3240", "3035");  // Eidsvoll
        KOMMUNER.put("3238", "3036");  // Nannestad
        KOMMUNER.put("3242", "3037");  // Hurdal
        KOMMUNER.put("3310", "3038");  // Hole
        KOMMUNER.put("3320", "3039");  // Flå
        KOMMUNER.put("3322", "3040");  // Nesbyen
        KOMMUNER.put("3324", "3041");  // Gol
        KOMMUNER.put("3326", "3042");  // Hemsedal
        KOMMUNER.put("3328", "3043");  // Ål
        KOMMUNER.put("3330", "3044");  // Hol
        KOMMUNER.put("3332", "3045");  // Sigdal
        KOMMUNER.put("3318", "3046");  // Krødsherad
        KOMMUNER.put("3316", "3047");  // Modum
        KOMMUNER.put("3314", "3048");  // Øvre Eiker
        KOMMUNER.put("3312", "3049");  // Lier
        KOMMUNER.put("3334", "3050");  // Flesberg
        KOMMUNER.put("3336", "3051");  // Rollag
        KOMMUNER.put("3338", "3052");  // Nore og Uvdal
        KOMMUNER.put("3236", "3053");  // Jevnaker
        KOMMUNER.put("3234", "3054");  // Lunner
        KOMMUNER.put("3901", "3801");  // Horten
        KOMMUNER.put("3903", "3802");  // Holmestrand
        KOMMUNER.put("3905", "3803");  // Tønsberg
        KOMMUNER.put("3907", "3804");  // Sandefjord
        KOMMUNER.put("3909", "3805");  // Larvik
        KOMMUNER.put("4001", "3806");  // Porsgrunn
        KOMMUNER.put("4003", "3807");  // Skien
        KOMMUNER.put("4005", "3808");  // Notodden
        KOMMUNER.put("3911", "3811");  // Færder
        KOMMUNER.put("4010", "3812");  // Siljan
        KOMMUNER.put("4012", "3813");  // Bamble
        KOMMUNER.put("4014", "3814");  // Kragerø
        KOMMUNER.put("4016", "3815");  // Drangedal
        KOMMUNER.put("4018", "3816");  // Nome
        KOMMUNER.put("4020", "3817");  // Midt-Telemark
        KOMMUNER.put("4026", "3818");  // Tinn
        KOMMUNER.put("4024", "3819");  // Hjartdal
        KOMMUNER.put("4022", "3820");  // Seljord
        KOMMUNER.put("4028", "3821");  // Kviteseid
        KOMMUNER.put("4030", "3822");  // Nissedal
        KOMMUNER.put("4032", "3823");  // Fyresdal
        KOMMUNER.put("4034", "3824");  // Tokke
        KOMMUNER.put("4036", "3825");  // Vinje
        KOMMUNER.put("5501", "5401");  // Tromsø
        KOMMUNER.put("5503", "5402");  // Harstad
        KOMMUNER.put("5601", "5403");  // Alta
        KOMMUNER.put("5634", "5404");  // Vardø
        KOMMUNER.put("5607", "5405");  // Vadsø
        KOMMUNER.put("5603", "5406");  // Hammerfest
        KOMMUNER.put("5510", "5411");  // Kvæfjord
        KOMMUNER.put("5512", "5412");  // Tjeldsund
        KOMMUNER.put("5514", "5413");  // Ibestad
        KOMMUNER.put("5516", "5414");  // Gratangen
        KOMMUNER.put("5518", "5415");  // Lavangen
        KOMMUNER.put("5520", "5416");  // Bardu
        KOMMUNER.put("5522", "5417");  // Salangen
        KOMMUNER.put("5524", "5418");  // Målselv
        KOMMUNER.put("5526", "5419");  // Sørreisa
        KOMMUNER.put("5528", "5420");  // Dyrøy
        KOMMUNER.put("5530", "5421");  // Senja
        KOMMUNER.put("5532", "5422");  // Balsfjord
        KOMMUNER.put("5534", "5423");  // Karlsøy
        KOMMUNER.put("5536", "5424");  // Lyngen
        KOMMUNER.put("5538", "5425");  // Storfjord
        KOMMUNER.put("5540", "5426");  // Kåfjord
        KOMMUNER.put("5542", "5427");  // Skjervøy
        KOMMUNER.put("5544", "5428");  // Nordreisa
        KOMMUNER.put("5546", "5429");  // Kvænangen
        KOMMUNER.put("5612", "5430");  // Kautokeino
        KOMMUNER.put("5614", "5432");  // Loppa
        KOMMUNER.put("5616", "5433");  // Hasvik
        KOMMUNER.put("5618", "5434");  // Måsøy
        KOMMUNER.put("5620", "5435");  // Nordkapp
        KOMMUNER.put("5622", "5436");  // Porsanger
        KOMMUNER.put("5610", "5437");  // Karasjok
        KOMMUNER.put("5624", "5438");  // Lebesby
        KOMMUNER.put("5626", "5439");  // Gamvik
        KOMMUNER.put("5630", "5440");  // Berlevåg
        KOMMUNER.put("5628", "5441");  // Tana
        KOMMUNER.put("5636", "5442");  // Nesseby
        KOMMUNER.put("5632", "5443");  // Båtsfjord
        KOMMUNER.put("5605", "5444");  // Sør-Varanger
    }

    private static Set<String> POSTNUMMER_HARAM = new HashSet<>();

    static {

        POSTNUMMER_HARAM.add("6264"); // TENNFJORD
        POSTNUMMER_HARAM.add("6265"); // VATNE
        POSTNUMMER_HARAM.add("6270"); // BRATTVÅG
        POSTNUMMER_HARAM.add("6272"); // HILDRE
        POSTNUMMER_HARAM.add("6280"); // SØVIK
        POSTNUMMER_HARAM.add("6281"); // SØVIK
        POSTNUMMER_HARAM.add("6282"); // BRATTVÅG
        POSTNUMMER_HARAM.add("6283"); // VATNE
        POSTNUMMER_HARAM.add("6285"); // STOREKALVØY
        POSTNUMMER_HARAM.add("6290"); // HARAMSØY
        POSTNUMMER_HARAM.add("6291"); // HARAMSØY
        POSTNUMMER_HARAM.add("6292"); // KJERSTAD
        POSTNUMMER_HARAM.add("6293"); // LONGVA
        POSTNUMMER_HARAM.add("6294"); // FJØRTOF
    }

    private static Set<String> POSTNUMMER_AALESUND = new HashSet<>();

    static {
        POSTNUMMER_AALESUND.add("6001");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6002");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6003");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6004");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6005");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6006");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6007");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6008");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6009");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6010");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6011");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6012");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6013");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6014");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6015");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6016");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6017");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6018");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6019");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6020");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6021");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6022");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6023");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6024");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6025");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6026");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6028");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6029");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6043");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6044");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6045");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6046");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6047");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6048");  // ÅLESUND
        POSTNUMMER_AALESUND.add("6057");  // ELLINGSØY
        POSTNUMMER_AALESUND.add("6240");  // ØRSKOG
        POSTNUMMER_AALESUND.add("6249");  // ØRSKOG
        POSTNUMMER_AALESUND.add("6260");  // SKODJE
        POSTNUMMER_AALESUND.add("6263");  // SKODJE
        POSTNUMMER_AALESUND.add("6286");  // ORTEN
        POSTNUMMER_AALESUND.add("6287");  // HARØY
        POSTNUMMER_AALESUND.add("6295");  // MYKLEBOST
        POSTNUMMER_AALESUND.add("6296");  // HARØY
        POSTNUMMER_AALESUND.add("6297");  // SANDØY
        POSTNUMMER_AALESUND.add("6298");  // ONA
        POSTNUMMER_AALESUND.add("6424");  // SANDØY
        POSTNUMMER_AALESUND.add("6426");  // ORTEN
        POSTNUMMER_AALESUND.add("6427");  // HARØY
        POSTNUMMER_AALESUND.add("6428");  // MYKLEBOST
    }

    private String getRandomPostnummer(Set<String> kommune) {

        return kommune.stream()
                .skip(postnummer.nextInt(kommune.size()))
                .findFirst()
                .orElse(null);
    }
    public String getRandomPostnummer(String kommune) {

        if (HARAM.equals(kommune)) {
            return getRandomPostnummer(POSTNUMMER_HARAM);

        } else if (AALESUND.equals(kommune)) {
            return getRandomPostnummer(POSTNUMMER_AALESUND);

        } else {
            return null;
        }
    }

    public String getKommuneNummer(String kommuneNummer) {

        return KOMMUNER.getOrDefault(kommuneNummer, kommuneNummer);
    }

    public boolean isGmlKommune(VegadresseDTO request, no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO response) {

        return isBlank(request.getKommunenummer()) ||
                Kor2024KommuneEndringer.getKommuneNummer(request.getKommunenummer()).equals(request.getKommunenummer());
    }

    public boolean isGmlKommune(MatrikkeladresseDTO request, no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO response) {

        return isBlank(request.getKommunenummer()) ||
                Kor2024KommuneEndringer.getKommuneNummer(request.getKommunenummer()).equals(request.getKommunenummer());
    }
}