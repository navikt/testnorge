package no.nav.dolly.util;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class CallIdUtil {

    public static String generateCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
