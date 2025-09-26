package no.nav.dolly.util;

import lombok.experimental.UtilityClass;

import java.time.Clock;
import java.time.ZoneId;

@UtilityClass
public class DateZoneUtil {

    public static final Clock CET = Clock.system(ZoneId.of("Europe/Oslo"));
}
