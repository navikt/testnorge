package no.nav.registre.bisys.consumer.ui;

import static no.nav.registre.bisys.consumer.ui.BisysUiNavigationSupport.bisysLogon;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import net.morher.ui.connect.html.HtmlApplicationUtils;
import no.nav.bidrag.dto.SynthesizedBidragRequest;
import no.nav.bidrag.exception.BidragRequestProcessingException;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.sak.Sak;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiSakConsumer;

@Slf4j
public class BisysUiConsumer {

  String saksbehandlerUid;
  String saksbehandlerPwd;
  String bisysUrl;
  String rolleSaksbehandler;
  int enhet;

  private BisysApplication bisys;

  private TestnorgeToBisysMapper testnorgeToBisysMapper;

  @Autowired private BisysUiSakConsumer bisysUiSakConsumer;

  public BisysUiConsumer(
      String saksbehandlerUid,
      String saksbehandlerPwd,
      String bisysUrl,
      String rolleSaksbehandler,
      String enhet) {

    this.saksbehandlerUid = saksbehandlerUid;
    this.saksbehandlerPwd = saksbehandlerPwd;
    this.bisysUrl = bisysUrl;
    this.rolleSaksbehandler = rolleSaksbehandler;
    this.enhet = Integer.valueOf(enhet);

    this.testnorgeToBisysMapper = Mappers.getMapper(TestnorgeToBisysMapper.class);
  }

  public void runCreateSoknad(SyntetisertBidragsmelding bidragsmelding)
      throws BidragRequestProcessingException {

    SynthesizedBidragRequest request = testnorgeToBisysMapper.testnorgeToBisys(bidragsmelding);

    log.info(
        "### Soknad creation started ### soknad related to child with ID {}, details: soknadstype {}, sokt om {}, soknad fra {}, mottatt dato {}, fra dato {}",
        request.getFnrBa(),
        request.getSoknadstype(),
        request.getSoktOm(),
        request.getSoknadFra(),
        request.getMottattDato(),
        request.getSoktFra());

    bisysLogon(bisysUrl, saksbehandlerUid, saksbehandlerPwd, rolleSaksbehandler, enhet);

    Sak sak = bisysUiSakConsumer.openOrCreateSak(bisys, request);

    log.info("html dump: {}", HtmlApplicationUtils.getHtml(sak));

    sak.nySoknad().click();
    bisys.soknad().fillInAndSaveSoknad(request);

    log.info(
        "### Soknad creation completed successfully ### soknad for child {} was created.",
        request.getFnrBa());
  }
}
