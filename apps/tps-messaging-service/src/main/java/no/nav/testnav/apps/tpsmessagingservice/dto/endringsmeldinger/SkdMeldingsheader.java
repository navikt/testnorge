package no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SkdMeldingsheader {

    private static final String KJORE_NUMMER = "000004421";
    private static final String KODE_SYSTEM = "TPSF";
    private static final String MQ_HANDLE = "000000000";
    private static final String SKD_REFERANSE = WhitespaceConstants.WHITESPACE_20_STK;

    private static final int INDEX_START_TILDELINGSKODE = 873;
    private static final int INDEX_SLUTT_TILDELINGSKODE = 874;

    private static final int INDEX_START_AARSAKSKODE = 26;
    private static final int INDEX_SLUTT_AARSAKSKODE = 28;

    private static final int INDEX_START_TRANSTYPE = 25;
    private static final int INDEX_SLUTT_TRANSTYPE = 26;

    public String appendHeader(String skdMelding) {

        var aarsakskode = extractAArsakskode(skdMelding);
        var transType = extractTranstype(skdMelding);
        var tildelingsKode = extractTildelingskode(skdMelding);

        var headerSkdMelding = new StringBuilder()
                .append(MQ_HANDLE)
                .append(KODE_SYSTEM)
                .append(KJORE_NUMMER)
                .append(aarsakskode)
                .append(transType)
                .append(tildelingsKode)
                .append(SKD_REFERANSE);

        return new StringBuilder(skdMelding)
                .reverse()
                .append(headerSkdMelding.reverse())
                .reverse()
                .toString();
    }

    private String extractAArsakskode(String skdMelding) {
        return skdMelding.length() > INDEX_SLUTT_AARSAKSKODE ? skdMelding.substring(INDEX_START_AARSAKSKODE, INDEX_SLUTT_AARSAKSKODE) : "  ";
    }

    private String extractTranstype(String skdMelding) {
        return skdMelding.length() > INDEX_SLUTT_TRANSTYPE ? skdMelding.substring(INDEX_START_TRANSTYPE, INDEX_SLUTT_TRANSTYPE) : " ";
    }

    private String extractTildelingskode(String skdMelding) {
        return skdMelding.length() > INDEX_SLUTT_TILDELINGSKODE ? skdMelding.substring(INDEX_START_TILDELINGSKODE, INDEX_SLUTT_TILDELINGSKODE) : "0";
    }
}
