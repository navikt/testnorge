package no.nav.dolly.consumer.kodeverk;

import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class Kor2024NyeKommuner {

    private static Set<String> KOMMUNER = new HashSet<>();

    static {
        KOMMUNER.add("1508");  // Ålesund
        KOMMUNER.add("1580");  // Haram
        KOMMUNER.add("3101");  // Halden
        KOMMUNER.add("3103");  // Moss
        KOMMUNER.add("3105");  // Sarpsborg
        KOMMUNER.add("3107");  // Fredrikstad
        KOMMUNER.add("3301");  // Drammen
        KOMMUNER.add("3303");  // Kongsberg
        KOMMUNER.add("3305");  // Ringerike
        KOMMUNER.add("3110");  // Hvaler
        KOMMUNER.add("3124");  // Aremark
        KOMMUNER.add("3122");  // Marker
        KOMMUNER.add("3118");  // Indre Østfold
        KOMMUNER.add("3116");  // Skiptvet
        KOMMUNER.add("3120");  // Rakkestad
        KOMMUNER.add("3112");  // Råde
        KOMMUNER.add("3114");  // Våler
        KOMMUNER.add("3216");  // Vestby
        KOMMUNER.add("3207");  // Nordre Follo
        KOMMUNER.add("3218");  // Ås
        KOMMUNER.add("3214");  // Frogn
        KOMMUNER.add("3212");  // Nesodden
        KOMMUNER.add("3201");  // Bærum
        KOMMUNER.add("3203");  // Asker
        KOMMUNER.add("3226");  // Aurskog-Høland
        KOMMUNER.add("3224");  // Rælingen
        KOMMUNER.add("3220");  // Enebakk
        KOMMUNER.add("3222");  // Lørenskog
        KOMMUNER.add("3205");  // Lillestrøm
        KOMMUNER.add("3232");  // Nittedal
        KOMMUNER.add("3230");  // Gjerdrum
        KOMMUNER.add("3209");  // Ullensaker
        KOMMUNER.add("3228");  // Nes
        KOMMUNER.add("3240");  // Eidsvoll
        KOMMUNER.add("3238");  // Nannestad
        KOMMUNER.add("3242");  // Hurdal
        KOMMUNER.add("3310");  // Hole
        KOMMUNER.add("3320");  // Flå
        KOMMUNER.add("3322");  // Nesbyen
        KOMMUNER.add("3324");  // Gol
        KOMMUNER.add("3326");  // Hemsedal
        KOMMUNER.add("3328");  // Ål
        KOMMUNER.add("3330");  // Hol
        KOMMUNER.add("3332");  // Sigdal
        KOMMUNER.add("3318");  // Krødsherad
        KOMMUNER.add("3316");  // Modum
        KOMMUNER.add("3314");  // Øvre Eiker
        KOMMUNER.add("3312");  // Lier
        KOMMUNER.add("3334");  // Flesberg
        KOMMUNER.add("3336");  // Rollag
        KOMMUNER.add("3338");  // Nore og Uvdal
        KOMMUNER.add("3236");  // Jevnaker
        KOMMUNER.add("3234");  // Lunner
        KOMMUNER.add("3901");  // Horten
        KOMMUNER.add("3903");  // Holmestrand
        KOMMUNER.add("3905");  // Tønsberg
        KOMMUNER.add("3907");  // Sandefjord
        KOMMUNER.add("3909");  // Larvik
        KOMMUNER.add("4001");  // Porsgrunn
        KOMMUNER.add("4003");  // Skien
        KOMMUNER.add("4005");  // Notodden
        KOMMUNER.add("3911");  // Færder
        KOMMUNER.add("4010");  // Siljan
        KOMMUNER.add("4012");  // Bamble
        KOMMUNER.add("4014");  // Kragerø
        KOMMUNER.add("4016");  // Drangedal
        KOMMUNER.add("4018");  // Nome
        KOMMUNER.add("4020");  // Midt-Telemark
        KOMMUNER.add("4026");  // Tinn
        KOMMUNER.add("4024");  // Hjartdal
        KOMMUNER.add("4022");  // Seljord
        KOMMUNER.add("4028");  // Kviteseid
        KOMMUNER.add("4030");  // Nissedal
        KOMMUNER.add("4032");  // Fyresdal
        KOMMUNER.add("4034");  // Tokke
        KOMMUNER.add("4036");  // Vinje
        KOMMUNER.add("5501");  // Tromsø
        KOMMUNER.add("5503");  // Harstad
        KOMMUNER.add("5601");  // Alta
        KOMMUNER.add("5634");  // Vardø
        KOMMUNER.add("5607");  // Vadsø
        KOMMUNER.add("5603");  // Hammerfest
        KOMMUNER.add("5510");  // Kvæfjord
        KOMMUNER.add("5512");  // Tjeldsund
        KOMMUNER.add("5514");  // Ibestad
        KOMMUNER.add("5516");  // Gratangen
        KOMMUNER.add("5518");  // Lavangen
        KOMMUNER.add("5520");  // Bardu
        KOMMUNER.add("5522");  // Salangen
        KOMMUNER.add("5524");  // Målselv
        KOMMUNER.add("5526");  // Sørreisa
        KOMMUNER.add("5528");  // Dyrøy
        KOMMUNER.add("5530");  // Senja
        KOMMUNER.add("5532");  // Balsfjord
        KOMMUNER.add("5534");  // Karlsøy
        KOMMUNER.add("5536");  // Lyngen
        KOMMUNER.add("5538");  // Storfjord
        KOMMUNER.add("5540");  // Kåfjord
        KOMMUNER.add("5542");  // Skjervøy
        KOMMUNER.add("5544");  // Nordreisa
        KOMMUNER.add("5546");  // Kvænangen
        KOMMUNER.add("5612");  // Kautokeino
        KOMMUNER.add("5614");  // Loppa
        KOMMUNER.add("5616");  // Hasvik
        KOMMUNER.add("5618");  // Måsøy
        KOMMUNER.add("5620");  // Nordkapp
        KOMMUNER.add("5622");  // Porsanger
        KOMMUNER.add("5610");  // Karasjok
        KOMMUNER.add("5624");  // Lebesby
        KOMMUNER.add("5626");  // Gamvik
        KOMMUNER.add("5630");  // Berlevåg
        KOMMUNER.add("5628");  // Tana
        KOMMUNER.add("5636");  // Nesseby
        KOMMUNER.add("5632");  // Båtsfjord
        KOMMUNER.add("5605");  // Sør-Varanger
    }

    public static boolean isNewKommune(String kommune) {

        return KOMMUNER.contains(kommune);
    }
}
