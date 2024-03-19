package no.nav.testnav.apps.tpsmessagingservice.utils;

import lombok.experimental.UtilityClass;

import static java.util.Objects.nonNull;

@UtilityClass
public final class NullcheckUtil {

    public static <T> T nullcheckSetDefaultValue(T value, T defaultValue) {
        return nonNull(value) ? value : defaultValue;
    }
}
