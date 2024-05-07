package no.nav.testnav.kodeverkservice.utility;

import lombok.experimental.UtilityClass;
import no.nav.testnav.kodeverkservice.dto.KodeverkBetydningerResponse;

@UtilityClass
public class FilterUtility {

    private static final String KOMMUNER2024 = "Kommuner2024";
    private static final String KOMMUNER = "Kommuner";

    public static String hentKodeverk(String kodeverk) {

        if (KOMMUNER2024.equals(kodeverk)) {
            return KOMMUNER;
        } else {
            return kodeverk;
        }
    }

    public static KodeverkBetydningerResponse filterKodeverk(String kodeverk, KodeverkBetydningerResponse response) {

        if (KOMMUNER2024.equals(kodeverk)) {
            return KommunerUtility.filterKommuner2024(response);
        } else {
            return response;
        }
    }
}
