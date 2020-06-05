package no.nav.registre.inst.properties;

public class HttpRequestConstants {

    private HttpRequestConstants() {
        throw new IllegalStateException("Utility klasse");
    }

    public static final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
}
