package no.nav.registre.bisys.exception;

import static no.nav.bidrag.ui.bisys.logging.BidragLoggingUtils.logAndDump;

import no.nav.bidrag.ui.bisys.common.BisysPage;

public class BidragRequestProcessingException extends Exception {

    private static final long serialVersionUID = -922689026150595262L;

    public BidragRequestProcessingException(String message, BisysPage bisysPage) {
        super(message);

        logAndDump(bisysPage);
    }

    public BidragRequestProcessingException(String message, BisysPage bisysPage, Exception t) {
        super(message, t);

        logAndDump(bisysPage);
    }

    public BidragRequestProcessingException(BisysPage bisysPage, Exception t) {
        super(String.format("Processing failed when processing page %s.", bisysPage.header().tittel()),
                t);

        logAndDump(bisysPage);
    }

}
