package no.nav.testnav.kodeverkservice.utility;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public final class CommonKeysAndUtils {

    public static final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
    public static final String CONSUMER = "Dolly";

    public static final String TEMAHISTARK = "TemaHistark";
    public static final String YRKESKLASSIFISERING = "Yrkesklassifisering";
    public static final String VERGEMAAL_FYLKESEMBETER = "Vergemål_Fylkesmannsembeter";

    public static final LocalDate GYLDIG_FRA = LocalDate.of(1900, 1, 1);
    public static final LocalDate GYLDIG_TIL = LocalDate.of(9999, 12, 31);
}
