package no.nav.dolly.util;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class NullcheckUtil {

    private NullcheckUtil() {
    }

    public static <T> T nullcheckSetDefaultValue(T value, T defaultValue) {
        return nonNull(value) ? value : defaultValue;
    }

    public static String blankcheckSetDefaultValue(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }
}
