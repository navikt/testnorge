package no.nav.registre.bisys.exception;

import static no.nav.bidrag.ui.bisys.logging.BidragLoggingUtils.logFeedbackErrors;
import static no.nav.bidrag.ui.bisys.logging.BidragLoggingUtils.logFeedbackMessages;
import static no.nav.bidrag.ui.bisys.logging.BidragLoggingUtils.logHtmlDump;

import no.nav.bidrag.ui.bisys.common.BisysPage;

public class BidragRequestProcessingException extends Exception {

    private static final long serialVersionUID = -922689026150595262L;

    public BidragRequestProcessingException(String message, BisysPage bisysPage) {
        super(message);
        logFeedbackMessages(bisysPage);
        logFeedbackErrors(bisysPage);
        logHtmlDump(bisysPage);
    }

    public BidragRequestProcessingException(String message, BisysPage bisysPage, Throwable t) {
        super(message, t);
        logFeedbackMessages(bisysPage);
        logFeedbackErrors(bisysPage);
        logHtmlDump(bisysPage);
    }

    public BidragRequestProcessingException(BisysPage bisysPage, Throwable t) {
        super(String.format("Processing failed when processing page %s.", bisysPage.header().tittel()),
                t);

        logFeedbackMessages(bisysPage);
        logFeedbackErrors(bisysPage);
        logHtmlDump(bisysPage);
    }

}
