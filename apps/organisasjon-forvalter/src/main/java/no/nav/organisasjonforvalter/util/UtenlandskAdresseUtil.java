package no.nav.organisasjonforvalter.util;

import lombok.experimental.UtilityClass;
import no.nav.organisasjonforvalter.dto.requests.BestillingRequest.AdresseRequest;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

@UtilityClass
public class UtenlandskAdresseUtil {

    private static final String ADRESSE_NAVN_NUMMER = "1KOLEJOWA 6/5";
    private static final String ADRESSE_BY_STED = "18-500 KOLNO";
    private static final String ADRESSE_3_UTLAND = "CAPITAL WEST";
    private static final String ADRESSE_POSTKODE = "3000";


    public static AdresseRequest prepareUtenlandskAdresse(AdresseRequest request) {

        if (request.getAdresselinjer().stream().anyMatch(Strings::isNotBlank)) {
            return request;
        }
        
        request.setAdresselinjer(List.of(ADRESSE_NAVN_NUMMER, ADRESSE_BY_STED));
        request.setPostnr(ADRESSE_POSTKODE);
        request.setPoststed(ADRESSE_3_UTLAND);
        request.setLandkode(request.getLandkode());
        return request;
    }
}