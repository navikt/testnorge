package no.nav.registre.bisys.consumer.ui;

import java.util.List;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.rs.request.BisysRequestAugments;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiFatteVedtakConsumer;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiSoknadConsumer;

@Slf4j
public class BisysUiConsumer {

  public static final String INCORRECT_ENTRY_PAGE = "Incorrect entry page";

  public static final String PROCESSING_FAILED = "Processing failed.";

  @Autowired
  private BisysUiSupport navigationSupport;

  @Autowired
  private BisysUiSoknadConsumer soknadConsumer;

  @Autowired
  private BisysUiFatteVedtakConsumer fatteVedtakConsumer;

  @Autowired
  private BisysRequestAugments bisysRequestAugments;


  private TestnorgeToBisysMapper testnorgeToBisysMapper =
      Mappers.getMapper(TestnorgeToBisysMapper.class);

  public void createVedtak(List<SyntetisertBidragsmelding> bidragsmeldinger)
      throws BidragRequestProcessingException {

    BisysApplication bisys = null;

    if (bidragsmeldinger != null && bidragsmeldinger.size() > 0) {
      bisys = navigationSupport.logon();
    }

    for (SyntetisertBidragsmelding bidragsmelding : bidragsmeldinger) {

      SynthesizedBidragRequest request =
          testnorgeToBisysMapper.testnorgeToBisys(bidragsmelding, bisysRequestAugments);
      log.info("Processing SyntetisertBidragsmelding with barnetsFnr {}",
          bidragsmelding.getBarnetsFnr());

      try {

        soknadConsumer.openOrCreateSoknad(bisys, request);
        fatteVedtakConsumer.runFatteVedtak(bisys, request);

      } catch (BidragRequestProcessingException brpe) {

        log.warn(
            "Processing failed for SyntetisertBidragsmelding with barnetsFnr {}, and mottattDato {}",
            bidragsmelding.getBarnetsFnr(), bidragsmelding.getMottattDato());

      }
    }
  }
}


