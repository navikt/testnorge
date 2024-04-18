package no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class WhitespaceConstants {

    public static final String WHITESPACE_5_STK = "     ";
    public static final String WHITESPACE_10_STK = WHITESPACE_5_STK + WHITESPACE_5_STK;
    public static final String WHITESPACE_20_STK = WHITESPACE_10_STK + WHITESPACE_10_STK;
    public static final String WHITESPACE_25_STK = WHITESPACE_5_STK + WHITESPACE_20_STK;
    public static final String WHITESPACE_40_STK = WHITESPACE_20_STK + WHITESPACE_20_STK;
    public static final String WHITESPACE_30_STK = WHITESPACE_5_STK + WHITESPACE_25_STK;
    public static final String WHITESPACE_50_STK = WHITESPACE_25_STK + WHITESPACE_25_STK;
    public static final String WHITESPACE_100_STK = WHITESPACE_50_STK + WHITESPACE_50_STK;

    public static final String FIVE_OES = "00000";
    public static final String SIX_OES = "000000";
    public static final String DUMMY_DATO = "00000000";
    public static final String DUMMY_IDENT = "00000000000";
}
