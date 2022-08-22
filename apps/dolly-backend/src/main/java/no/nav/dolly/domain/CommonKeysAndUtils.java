package no.nav.dolly.domain;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class CommonKeysAndUtils {

    public static final String MDC_CONSUMER_ID_KEY = "consumerId";
    public static final String MDC_CALL_ID_KEY = "callId";

    public static final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
    public static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    public static final String CONSUMER = "Dolly";

}
