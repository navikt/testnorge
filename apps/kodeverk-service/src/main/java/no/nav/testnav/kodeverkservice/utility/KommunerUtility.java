package no.nav.testnav.kodeverkservice.utility;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import no.nav.testnav.kodeverkservice.dto.KodeverkBetydningerResponse;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class KommunerUtility {

    @Getter
    private static final Set<String> gamleKommunenummer = new HashSet<>();

    static {
        gamleKommunenummer.add("3043"); // Ål
        gamleKommunenummer.add("1507"); // Ålesund
        gamleKommunenummer.add("5403"); // Alta
        gamleKommunenummer.add("3012"); // Aremark
        gamleKommunenummer.add("3021"); // Ås
        gamleKommunenummer.add("3025"); // Asker
        gamleKommunenummer.add("3026"); // Aurskog-Høland
        gamleKommunenummer.add("3024"); // Bærum
        gamleKommunenummer.add("5422"); // Balsfjord
        gamleKommunenummer.add("3813"); // Bamble
        gamleKommunenummer.add("5416"); // Bardu
        gamleKommunenummer.add("5443"); // Båtsfjord
        gamleKommunenummer.add("5440"); // Berlevåg
        gamleKommunenummer.add("3005"); // Drammen
        gamleKommunenummer.add("3815"); // Drangedal
        gamleKommunenummer.add("5420"); // Dyrøy
        gamleKommunenummer.add("3035"); // Eidsvoll
        gamleKommunenummer.add("3028"); // Enebakk
        gamleKommunenummer.add("3811"); // Færder
        gamleKommunenummer.add("3039"); // Flå
        gamleKommunenummer.add("3050"); // Flesberg
        gamleKommunenummer.add("3004"); // Fredrikstad
        gamleKommunenummer.add("3022"); // Frogn
        gamleKommunenummer.add("3823"); // Fyresdal
        gamleKommunenummer.add("5439"); // Gamvik
        gamleKommunenummer.add("3032"); // Gjerdrum
        gamleKommunenummer.add("3041"); // Gol
        gamleKommunenummer.add("5414"); // Gratangen
        gamleKommunenummer.add("3001"); // Halden
        gamleKommunenummer.add("5406"); // Hammerfest
        gamleKommunenummer.add("5402"); // Harstad
        gamleKommunenummer.add("5433"); // Hasvik
        gamleKommunenummer.add("3042"); // Hemsedal
        gamleKommunenummer.add("1515"); // Herøy
        gamleKommunenummer.add("3819"); // Hjartdal
        gamleKommunenummer.add("3044"); // Hol
        gamleKommunenummer.add("3038"); // Hole
        gamleKommunenummer.add("3802"); // Holmestrand
        gamleKommunenummer.add("3801"); // Horten
        gamleKommunenummer.add("3037"); // Hurdal
        gamleKommunenummer.add("3011"); // Hvaler
        gamleKommunenummer.add("5413"); // Ibestad
        gamleKommunenummer.add("3014"); // Indre Østfold
        gamleKommunenummer.add("3053"); // Jevnaker
        gamleKommunenummer.add("5423"); // Karlsøy
        gamleKommunenummer.add("3006"); // Kongsberg
        gamleKommunenummer.add("3814"); // Kragerø
        gamleKommunenummer.add("3046"); // Krødsherad
        gamleKommunenummer.add("5411"); // Kvæfjord
        gamleKommunenummer.add("5429"); // Kvænangen
        gamleKommunenummer.add("3821"); // Kviteseid
        gamleKommunenummer.add("3805"); // Larvik
        gamleKommunenummer.add("5438"); // Lebesby
        gamleKommunenummer.add("3049"); // Lier
        gamleKommunenummer.add("3030"); // Lillestrøm
        gamleKommunenummer.add("5432"); // Loppa
        gamleKommunenummer.add("3029"); // Lørenskog
        gamleKommunenummer.add("3054"); // Lunner
        gamleKommunenummer.add("5424"); // Lyngen
        gamleKommunenummer.add("5418"); // Målselv
        gamleKommunenummer.add("3013"); // Marker
        gamleKommunenummer.add("5434"); // Måsøy
        gamleKommunenummer.add("3817"); // Midt-Telemark
        gamleKommunenummer.add("3047"); // Modum
        gamleKommunenummer.add("3002"); // Moss
        gamleKommunenummer.add("3036"); // Nannestad
        gamleKommunenummer.add("3034"); // Nes
        gamleKommunenummer.add("3040"); // Nesbyen
        gamleKommunenummer.add("3023"); // Nesodden
        gamleKommunenummer.add("3822"); // Nissedal
        gamleKommunenummer.add("3031"); // Nittedal
        gamleKommunenummer.add("3816"); // Nome
        gamleKommunenummer.add("5435"); // Nordkapp
        gamleKommunenummer.add("3020"); // Nordre Follo
        gamleKommunenummer.add("3052"); // Nore og Uvdal
        gamleKommunenummer.add("3808"); // Notodden
        gamleKommunenummer.add("3048"); // Øvre Eiker
        gamleKommunenummer.add("3806"); // Porsgrunn
        gamleKommunenummer.add("3017"); // Råde
        gamleKommunenummer.add("3027"); // Rælingen
        gamleKommunenummer.add("3016"); // Rakkestad
        gamleKommunenummer.add("3007"); // Ringerike
        gamleKommunenummer.add("3051"); // Rollag
        gamleKommunenummer.add("5417"); // Salangen
        gamleKommunenummer.add("3804"); // Sandefjord
        gamleKommunenummer.add("3003"); // Sarpsborg
        gamleKommunenummer.add("3820"); // Seljord
        gamleKommunenummer.add("5421"); // Senja
        gamleKommunenummer.add("3045"); // Sigdal
        gamleKommunenummer.add("3812"); // Siljan
        gamleKommunenummer.add("3807"); // Skien
        gamleKommunenummer.add("3015"); // Skiptvet
        gamleKommunenummer.add("5427"); // Skjervøy
        gamleKommunenummer.add("5419"); // Sørreisa
        gamleKommunenummer.add("5444"); // Sør-Varanger
        gamleKommunenummer.add("3818"); // Tinn
        gamleKommunenummer.add("5412"); // Tjeldsund
        gamleKommunenummer.add("3824"); // Tokke
        gamleKommunenummer.add("3803"); // Tønsberg
        gamleKommunenummer.add("5401"); // Tromsø
        gamleKommunenummer.add("3033"); // Ullensaker
        gamleKommunenummer.add("5405"); // Vadsø
        gamleKommunenummer.add("5404"); // Vardø
        gamleKommunenummer.add("3019"); // Vestby
        gamleKommunenummer.add("3825"); // Vinje
    }
    public static KodeverkBetydningerResponse filterKommuner2024(KodeverkBetydningerResponse response) {

        return KodeverkBetydningerResponse.builder()
                .betydninger(response.getBetydninger().entrySet().stream()
                        .filter(entry -> !gamleKommunenummer.contains(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .build();
    }
}