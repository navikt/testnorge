package no.nav.dolly.util;

import java.util.UUID;

public final class CallIdUtil {
    private CallIdUtil() {
    }

    public static String generateCallId() {
        return UUID.randomUUID().toString();
    }
}
