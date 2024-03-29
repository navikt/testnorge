package no.nav.organisasjonforvalter.util;

import lombok.experimental.UtilityClass;
import no.nav.organisasjonforvalter.dto.requests.BestillingRequest.AdresseRequest;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@UtilityClass
public class UtenlandskAdresseUtil {

    private static final String ADRESSE_NAVN_NUMMER = "1KOLEJOWA 6";
    private static final String ADRESSE_BY_STED = "18500 KOLNO";
    private static final String ADRESSE_3_UTLAND = "CAPITAL WEST";
    private static final String ADRESSE_POSTKODE = "3000";


    public static void prepareUtenlandskAdresse(AdresseRequest request) {

        if (isBlank(request.getPostnr())) {
            request.setPostnr(ADRESSE_POSTKODE);
        }
        if (isBlank(request.getPoststed())) {
            request.setPoststed(ADRESSE_3_UTLAND);
        }

        if (request.getAdresselinjer().stream().anyMatch(s -> !s.isBlank())) {
            return;
        }

        request.setAdresselinjer(List.of(ADRESSE_NAVN_NUMMER, ADRESSE_BY_STED));
    }
}
