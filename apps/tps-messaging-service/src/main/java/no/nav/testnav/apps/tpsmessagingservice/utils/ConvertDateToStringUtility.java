package no.nav.testnav.apps.tpsmessagingservice.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.nonNull;

@UtilityClass
public final class ConvertDateToStringUtility {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss");

    public static String yyyyMMdd(LocalDateTime localDateTime) {

        return nonNull(localDateTime) ? localDateTime.format(DATE_FORMAT) : null;
    }

    public static String hhMMss(LocalDateTime localDateTime) {

        return nonNull(localDateTime) ? localDateTime.format(TIME_FORMAT) : null;
    }
}
