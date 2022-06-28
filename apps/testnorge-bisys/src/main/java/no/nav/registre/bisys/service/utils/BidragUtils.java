package no.nav.registre.bisys.service.utils;

import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.registre.bisys.consumer.response.SyntetisertBidragsmelding;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BidragUtils {

    private BidragUtils(){

    }

    private static final String FORMAT_WARNING = "soktFra incorrectly formatted as {} in SyntetisertBidragsmelding, attempting to parse string as floating number";

    public static boolean isSoktOm18(String soktOm) {
        return KodeSoknGrKomConstants.BIDRAG_18_AAR.equals(soktOm) || KodeSoknGrKomConstants.BIDRAG_18_AAR_INNKREVING.equals(soktOm);
    }

    public static int parseSoktFra(SyntetisertBidragsmelding bidragsmelding) {
        try {
            return Integer.parseInt(bidragsmelding.getSoktFra());
        } catch (NumberFormatException e) {
            log.warn(FORMAT_WARNING, bidragsmelding.getSoktFra());
            return (int) Float.parseFloat(bidragsmelding.getSoktFra());
        }
    }

}
