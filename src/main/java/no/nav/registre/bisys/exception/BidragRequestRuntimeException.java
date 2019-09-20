package no.nav.registre.bisys.exception;

import static no.nav.bidrag.ui.bisys.logging.BidragLoggingUtils.logAndDump;

import no.nav.bidrag.ui.bisys.common.BisysPage;

public class BidragRequestRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -322689025728595262L;

    private static final String ERROR_MSG = "Irrecoverable error occurred during processing of page %s.";

    public BidragRequestRuntimeException(String message, BisysPage bisysPage) {
        super(message);
        logAndDump(bisysPage);
    }

    public BidragRequestRuntimeException(BisysPage bisysPage, Throwable t) {
        super(String.format(ERROR_MSG, bisysPage.header().tittel()), t);
        logAndDump(bisysPage);
    }

    public BidragRequestRuntimeException(String errorMsg) {
        super(errorMsg);
    }

}
