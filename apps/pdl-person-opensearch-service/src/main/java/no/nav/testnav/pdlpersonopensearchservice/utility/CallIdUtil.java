package no.nav.testnav.pdlpersonopensearchservice.utility;

import lombok.experimental.UtilityClass;

import java.util.UUID;

import static java.lang.String.format;

@UtilityClass
public final class CallIdUtil {
    public static final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
    public static final String CONSUMER = "Dolly";
    public static String generateCallId() {
        return format("%s-%s", CONSUMER, UUID.randomUUID());
    }
}