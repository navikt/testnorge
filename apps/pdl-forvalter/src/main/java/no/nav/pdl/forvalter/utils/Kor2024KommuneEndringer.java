package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.data.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VegadresseDTO;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

@UtilityClass
public class Kor2024KommuneEndringer {

    private static final String HARAM = "1580";
    private static final String AALESUND = "1508";
    private static Random postnummer = new SecureRandom();

    private static Map<String, String> kommuner = new HashMap<>();

    static {
        kommuner.put("1508", "1507");  // Ålesund
        kommuner.put("1580", "1507");  // Haram
        kommuner.put("3101", "3001");  // Halden
        kommuner.put("3103", "3002");  // Moss
        kommuner.put("3105", "3003");  // Sarpsborg
        kommuner.put("3107", "3004");  // Fredrikstad
        kommuner.put("3301", "3005");  // Drammen
        kommuner.put("3303", "3006");  // Kongsberg
        kommuner.put("3305", "3007");  // Ringerike
        kommuner.put("3110", "3011");  // Hvaler
        kommuner.put("3124", "3012");  // Aremark
        kommuner.put("3122", "3013");  // Marker
        kommuner.put("3118", "3014");  // Indre Østfold
        kommuner.put("3116", "3015");  // Skiptvet
        kommuner.put("3120", "3016");  // Rakkestad
        kommuner.put("3112", "3017");  // Råde
        kommuner.put("3114", "3018");  // Våler
        kommuner.put("3216", "3019");  // Vestby
        kommuner.put("3207", "3020");  // Nordre Follo
        kommuner.put("3218", "3021");  // Ås
        kommuner.put("3214", "3022");  // Frogn
        kommuner.put("3212", "3023");  // Nesodden
        kommuner.put("3201", "3024");  // Bærum
        kommuner.put("3203", "3025");  // Asker
        kommuner.put("3226", "3026");  // Aurskog-Høland
        kommuner.put("3224", "3027");  // Rælingen
        kommuner.put("3220", "3028");  // Enebakk
        kommuner.put("3222", "3029");  // Lørenskog
        kommuner.put("3205", "3030");  // Lillestrøm
        kommuner.put("3232", "3031");  // Nittedal
        kommuner.put("3230", "3032");  // Gjerdrum
        kommuner.put("3209", "3033");  // Ullensaker
        kommuner.put("3228", "3034");  // Nes
        kommuner.put("3240", "3035");  // Eidsvoll
        kommuner.put("3238", "3036");  // Nannestad
        kommuner.put("3242", "3037");  // Hurdal
        kommuner.put("3310", "3038");  // Hole
        kommuner.put("3320", "3039");  // Flå
        kommuner.put("3322", "3040");  // Nesbyen
        kommuner.put("3324", "3041");  // Gol
        kommuner.put("3326", "3042");  // Hemsedal
        kommuner.put("3328", "3043");  // Ål
        kommuner.put("3330", "3044");  // Hol
        kommuner.put("3332", "3045");  // Sigdal
        kommuner.put("3318", "3046");  // Krødsherad
        kommuner.put("3316", "3047");  // Modum
        kommuner.put("3314", "3048");  // Øvre Eiker
        kommuner.put("3312", "3049");  // Lier
        kommuner.put("3334", "3050");  // Flesberg
        kommuner.put("3336", "3051");  // Rollag
        kommuner.put("3338", "3052");  // Nore og Uvdal
        kommuner.put("3236", "3053");  // Jevnaker
        kommuner.put("3234", "3054");  // Lunner
        kommuner.put("3901", "3801");  // Horten
        kommuner.put("3903", "3802");  // Holmestrand
        kommuner.put("3905", "3803");  // Tønsberg
        kommuner.put("3907", "3804");  // Sandefjord
        kommuner.put("3909", "3805");  // Larvik
        kommuner.put("4001", "3806");  // Porsgrunn
        kommuner.put("4003", "3807");  // Skien
        kommuner.put("4005", "3808");  // Notodden
        kommuner.put("3911", "3811");  // Færder
        kommuner.put("4010", "3812");  // Siljan
        kommuner.put("4012", "3813");  // Bamble
        kommuner.put("4014", "3814");  // Kragerø
        kommuner.put("4016", "3815");  // Drangedal
        kommuner.put("4018", "3816");  // Nome
        kommuner.put("4020", "3817");  // Midt-Telemark
        kommuner.put("4026", "3818");  // Tinn
        kommuner.put("4024", "3819");  // Hjartdal
        kommuner.put("4022", "3820");  // Seljord
        kommuner.put("4028", "3821");  // Kviteseid
        kommuner.put("4030", "3822");  // Nissedal
        kommuner.put("4032", "3823");  // Fyresdal
        kommuner.put("4034", "3824");  // Tokke
        kommuner.put("4036", "3825");  // Vinje
        kommuner.put("5501", "5401");  // Tromsø
        kommuner.put("5503", "5402");  // Harstad
        kommuner.put("5601", "5403");  // Alta
        kommuner.put("5634", "5404");  // Vardø
        kommuner.put("5607", "5405");  // Vadsø
        kommuner.put("5603", "5406");  // Hammerfest
        kommuner.put("5510", "5411");  // Kvæfjord
        kommuner.put("5512", "5412");  // Tjeldsund
        kommuner.put("5514", "5413");  // Ibestad
        kommuner.put("5516", "5414");  // Gratangen
        kommuner.put("5518", "5415");  // Lavangen
        kommuner.put("5520", "5416");  // Bardu
        kommuner.put("5522", "5417");  // Salangen
        kommuner.put("5524", "5418");  // Målselv
        kommuner.put("5526", "5419");  // Sørreisa
        kommuner.put("5528", "5420");  // Dyrøy
        kommuner.put("5530", "5421");  // Senja
        kommuner.put("5532", "5422");  // Balsfjord
        kommuner.put("5534", "5423");  // Karlsøy
        kommuner.put("5536", "5424");  // Lyngen
        kommuner.put("5538", "5425");  // Storfjord
        kommuner.put("5540", "5426");  // Kåfjord
        kommuner.put("5542", "5427");  // Skjervøy
        kommuner.put("5544", "5428");  // Nordreisa
        kommuner.put("5546", "5429");  // Kvænangen
        kommuner.put("5612", "5430");  // Kautokeino
        kommuner.put("5614", "5432");  // Loppa
        kommuner.put("5616", "5433");  // Hasvik
        kommuner.put("5618", "5434");  // Måsøy
        kommuner.put("5620", "5435");  // Nordkapp
        kommuner.put("5622", "5436");  // Porsanger
        kommuner.put("5610", "5437");  // Karasjok
        kommuner.put("5624", "5438");  // Lebesby
        kommuner.put("5626", "5439");  // Gamvik
        kommuner.put("5630", "5440");  // Berlevåg
        kommuner.put("5628", "5441");  // Tana
        kommuner.put("5636", "5442");  // Nesseby
        kommuner.put("5632", "5443");  // Båtsfjord
        kommuner.put("5605", "5444");  // Sør-Varanger
    }

    private static Set<String> postnummerHaram = new HashSet<>();

    static {

        postnummerHaram.add("6264"); // TENNFJORD
        postnummerHaram.add("6265"); // VATNE
        postnummerHaram.add("6270"); // BRATTVÅG
        postnummerHaram.add("6272"); // HILDRE
        postnummerHaram.add("6280"); // SØVIK
        postnummerHaram.add("6281"); // SØVIK
        postnummerHaram.add("6282"); // BRATTVÅG
        postnummerHaram.add("6283"); // VATNE
        postnummerHaram.add("6285"); // STOREKALVØY
        postnummerHaram.add("6290"); // HARAMSØY
        postnummerHaram.add("6291"); // HARAMSØY
        postnummerHaram.add("6292"); // KJERSTAD
        postnummerHaram.add("6293"); // LONGVA
        postnummerHaram.add("6294"); // FJØRTOF
    }

    private static Set<String> postnummerAalesund = new HashSet<>();

    static {
        postnummerAalesund.add("6001");  // ÅLESUND
        postnummerAalesund.add("6002");  // ÅLESUND
        postnummerAalesund.add("6003");  // ÅLESUND
        postnummerAalesund.add("6004");  // ÅLESUND
        postnummerAalesund.add("6005");  // ÅLESUND
        postnummerAalesund.add("6006");  // ÅLESUND
        postnummerAalesund.add("6007");  // ÅLESUND
        postnummerAalesund.add("6008");  // ÅLESUND
        postnummerAalesund.add("6009");  // ÅLESUND
        postnummerAalesund.add("6010");  // ÅLESUND
        postnummerAalesund.add("6011");  // ÅLESUND
        postnummerAalesund.add("6012");  // ÅLESUND
        postnummerAalesund.add("6013");  // ÅLESUND
        postnummerAalesund.add("6014");  // ÅLESUND
        postnummerAalesund.add("6015");  // ÅLESUND
        postnummerAalesund.add("6016");  // ÅLESUND
        postnummerAalesund.add("6017");  // ÅLESUND
        postnummerAalesund.add("6018");  // ÅLESUND
        postnummerAalesund.add("6019");  // ÅLESUND
        postnummerAalesund.add("6020");  // ÅLESUND
        postnummerAalesund.add("6021");  // ÅLESUND
        postnummerAalesund.add("6022");  // ÅLESUND
        postnummerAalesund.add("6023");  // ÅLESUND
        postnummerAalesund.add("6024");  // ÅLESUND
        postnummerAalesund.add("6025");  // ÅLESUND
        postnummerAalesund.add("6026");  // ÅLESUND
        postnummerAalesund.add("6028");  // ÅLESUND
        postnummerAalesund.add("6029");  // ÅLESUND
        postnummerAalesund.add("6043");  // ÅLESUND
        postnummerAalesund.add("6044");  // ÅLESUND
        postnummerAalesund.add("6045");  // ÅLESUND
        postnummerAalesund.add("6046");  // ÅLESUND
        postnummerAalesund.add("6047");  // ÅLESUND
        postnummerAalesund.add("6048");  // ÅLESUND
        postnummerAalesund.add("6057");  // ELLINGSØY
        postnummerAalesund.add("6240");  // ØRSKOG
        postnummerAalesund.add("6249");  // ØRSKOG
        postnummerAalesund.add("6260");  // SKODJE
        postnummerAalesund.add("6263");  // SKODJE
        postnummerAalesund.add("6286");  // ORTEN
        postnummerAalesund.add("6287");  // HARØY
        postnummerAalesund.add("6295");  // MYKLEBOST
        postnummerAalesund.add("6296");  // HARØY
        postnummerAalesund.add("6297");  // SANDØY
        postnummerAalesund.add("6298");  // ONA
        postnummerAalesund.add("6424");  // SANDØY
        postnummerAalesund.add("6426");  // ORTEN
        postnummerAalesund.add("6427");  // HARØY
        postnummerAalesund.add("6428");  // MYKLEBOST
    }

    private String getRandomPostnummer(Set<String> kommune) {

        return kommune.stream()
                .skip(postnummer.nextInt(kommune.size()))
                .findFirst()
                .orElse(null);
    }
    public String getRandomPostnummer(String kommune) {

        if (HARAM.equals(kommune)) {
            return getRandomPostnummer(postnummerHaram);

        } else if (AALESUND.equals(kommune)) {
            return getRandomPostnummer(postnummerAalesund);

        } else {
            return null;
        }
    }

    public String getKommuneNummer(String kommuneNummer) {

        return kommuner.getOrDefault(kommuneNummer, kommuneNummer);
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