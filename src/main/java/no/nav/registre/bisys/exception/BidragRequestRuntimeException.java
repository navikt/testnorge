package no.nav.registre.bisys.exception;

import static no.nav.bidrag.ui.bisys.logging.BidragLoggingUtils.logFeedbackErrors;
import static no.nav.bidrag.ui.bisys.logging.BidragLoggingUtils.logFeedbackMessages;
import static no.nav.bidrag.ui.bisys.logging.BidragLoggingUtils.logHtmlDump;

import no.nav.bidrag.ui.bisys.common.BisysPage;

public class BidragRequestRuntimeException extends RuntimeException {

  private static final long serialVersionUID = -322689025728595262L;

  private static final String ERROR_MSG =
      "Irrecoverable error occurred during processing of page %s.";

  public BidragRequestRuntimeException(String message, BisysPage bisysPage) {
    super(message);
    logFeedbackMessages(bisysPage);
    logFeedbackErrors(bisysPage);
    logHtmlDump(bisysPage);
  }

  public BidragRequestRuntimeException(BisysPage bisysPage, Throwable t) {
    super(String.format(ERROR_MSG, bisysPage.header().tittel()), t);
    logFeedbackMessages(bisysPage);
    logFeedbackErrors(bisysPage);
    logHtmlDump(bisysPage);
  }

  public BidragRequestRuntimeException(String errorMsg) {
    super(errorMsg);
  }

}
