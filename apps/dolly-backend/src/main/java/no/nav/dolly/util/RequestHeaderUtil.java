package no.nav.dolly.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@UtilityClass
public class RequestHeaderUtil {

    public static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
