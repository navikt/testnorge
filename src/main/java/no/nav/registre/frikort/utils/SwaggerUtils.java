package no.nav.registre.frikort.utils;

public class SwaggerUtils {

    public static final String REQUEST_BODY_DESCRIPTION = "JSON som inneholder følgende key-value par: \n\n " +
            "Key: ID-nummer - Value: antall meldinger som skal generes for ID-nummer \n\n" +
            "Example: \"1234567910\": 3";
    public static final String LEGG_PAA_KOE_DESCRIPTION = "Avgjør om genererte meldinger skal legges til på MQ kø " +
            "eller ikke.";

}
