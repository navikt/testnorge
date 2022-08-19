package no.nav.testnav.apps.tpservice.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

import static java.lang.String.format;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.CONSUMER;


@UtilityClass
public final class CallIdUtil {

    public static String generateCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
