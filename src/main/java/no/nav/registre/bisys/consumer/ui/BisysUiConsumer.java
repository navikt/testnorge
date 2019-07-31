package no.nav.registre.bisys.consumer.ui;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import com.gargoylesoftware.htmlunit.WebClient;
import lombok.extern.slf4j.Slf4j;
import net.morher.ui.connect.api.ApplicationDefinition;
import net.morher.ui.connect.api.listener.ActionLogger;
import net.morher.ui.connect.html.HtmlApplicationUtils;
import net.morher.ui.connect.html.HtmlMapper;
import net.morher.ui.connect.html.listener.WaitForJavaScriptListener;
import net.morher.ui.connect.http.Browser;
import net.morher.ui.connect.http.BrowserConfigurer;
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

    bisysLogon();

    // TODO: Introdusere mapping mellom saksbehandlers NAV-kontor og synt.brukers tilknyttede enhet
    // Velg pålogget enhet
    bisys.velgGruppe().velgGruppe(rolleSaksbehandler);
    bisys.velgEnhet().velgEnhet(enhet);

    Sak sak = bisysUiSakConsumer.openOrCreateSak(bisys, request);

    log.info("html dump: {}", HtmlApplicationUtils.getHtml(sak));

    sak.nySoknad().click();
    bisys.soknad().fillInAndSaveSoknad(request);

    log.info(
        "### Soknad creation completed successfully ### soknad for child {} was created.",
        request.getFnrBa());
  }

  private void bisysLogon() {
    bisys = openBrowser(bisysUrl);
    log.info("html dump: {}", HtmlApplicationUtils.getHtml(bisys.openamLoginPage()));
    bisys.openamLoginPage().signIn(saksbehandlerUid, saksbehandlerPwd);
  }

  private BisysApplication openBrowser(String url) {
    return ApplicationDefinition.of(BisysApplication.class)
        .mapWith(new HtmlMapper())
        .addListener(new WaitForJavaScriptListener())
        .addListener(new ActionLogger())
        .connect(
            new Browser()
                .asChrome()
                .useInsecureSSL()
                .addConfigurer(new BisysBrowserConfigurer())
                .openUrl(url));
  }

  private static class BisysBrowserConfigurer implements BrowserConfigurer {

    @Override
    public void configure(WebClient client) {
      client.getOptions().setThrowExceptionOnScriptError(false);
    }
  }
}
