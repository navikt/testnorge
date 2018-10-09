package no.nav.dolly.util;

import java.util.UUID;

public class CallIdUtil {

    public static String generateCallId() {
        return UUID.randomUUID().toString();
    }
}
