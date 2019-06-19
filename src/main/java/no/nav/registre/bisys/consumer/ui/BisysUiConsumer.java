package no.nav.registre.bisys.consumer.ui;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mapstruct.factory.Mappers;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import lombok.extern.slf4j.Slf4j;
import net.morher.ui.connect.api.ApplicationDefinition;
import net.morher.ui.connect.api.element.Button;
import net.morher.ui.connect.api.element.Label;
import net.morher.ui.connect.api.listener.ActionLogger;
import net.morher.ui.connect.html.HtmlApplicationUtils;
import net.morher.ui.connect.html.HtmlMapper;
import net.morher.ui.connect.html.listener.WaitForJavaScriptListener;
import net.morher.ui.connect.http.Browser;
import net.morher.ui.connect.http.BrowserConfigurer;
import no.nav.bidrag.dto.SynthesizedBidragRequest;
import no.nav.bidrag.exception.BidragRequestProcessingException;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.brukeroversikt.Brukeroversikt;
import no.nav.bidrag.ui.bisys.brukeroversikt.Ytelseslinje;
import no.nav.bidrag.ui.bisys.common.BisysPage;
import no.nav.bidrag.ui.bisys.rolle.Person;
import no.nav.bidrag.ui.bisys.rolle.RolleBarn;
import no.nav.bidrag.ui.bisys.rolle.Roller;
import no.nav.bidrag.ui.bisys.rolle.Samhandler;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;

@Slf4j
public class BisysUiConsumer {

  String saksbehandlerUid;
  String saksbehandlerPwd;
  String bisysUrl;
  String rolleSaksbehandler;
  int enhet;

  private BisysApplication bisys;

  private TestnorgeToBisysMapper testnorgeToBisysMapper;

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

  private static void debug(BisysPage page) {
    log.debug(HtmlApplicationUtils.getHtml(page));
  }

  public void runCreateSoknad(SyntetisertBidragsmelding bidragsmelding)
      throws BidragRequestProcessingException {

    SynthesizedBidragRequest request = testnorgeToBisysMapper.testnorgeToBisys(bidragsmelding);

    bisys = bisysLogon();

    // TODO: Introdusere mapping mellom saksbehandlers NAV-kontor og synt.brukers tilknyttede enhet
    // Velg pålogget enhet
    bisys.velgGruppe().velgGruppe(rolleSaksbehandler);

    bisys.velgEnhet().velgEnhet(enhet);

    Optional<String> existingSaksnr = findExistingSakInBrukeroversikt(request);

    if (existingSaksnr.isPresent()) getSakAndGoToSoknad(existingSaksnr.get(), request);
    else {
      bisys.sak().nySak().click();
      createRollerAndGoToSoknad(bisys.roller(), request, false);
    }
    bisys.soknad().fillInAndSaveSoknad(request);
  }

  private void getSakAndGoToSoknad(String saksnr, SynthesizedBidragRequest request) {

    // Fill in saksnr
    bisys.sak().sokSaksnr().setValue(saksnr);

    // Click "Hent"
    bisys.sak().hentSak().click();

    // Click "Ny søknad"
    bisys.sak().nySoknad().click();
  }

  private BisysApplication bisysLogon() {
    BisysApplication bisys = openBrowser(bisysUrl);
    log.debug(HtmlApplicationUtils.getHtml(bisys.openamLoginPage()));
    bisys.openamLoginPage().signIn(saksbehandlerUid, saksbehandlerPwd);
    log.debug(HtmlApplicationUtils.getHtml(bisys.openamLoginPage()));
    return bisys;
  }

  private Optional<String> findExistingSakInBrukeroversikt(SynthesizedBidragRequest request) {
    bisys.oppgaveliste().header().velgSkjermbilde().select("Brukeroversikt");

    bisys.brukeroversikt().fnr().setValue(request.getFnrBa());

    bisys.brukeroversikt().hent().click();

    List<Ytelseslinje> ytelser = bisys.brukeroversikt().ytelser();

    if (ytelser == null || ytelser.isEmpty()) {
      redirectToSak(bisys.brukeroversikt());
      return Optional.empty();
    }

    for (Ytelseslinje ytelse : ytelser) {
      String status;
      try {
        status = ytelse.status().getText();
      } catch (ElementNotFoundException | NoSuchElementException ee) {
        System.out.println("Ingen ytelser funnet på barn " + request.getFnrBa());

        redirectToSak(bisys.brukeroversikt());
        return Optional.empty();
      }
      if (sakActive(status)
          && existingSakContainsSameBpBMAsRequest(
              ytelse.saksnr().getText(), ytelse.linkToSak(), request)) {

        redirectToSak(bisys.brukeroversikt());
        return Optional.of(ytelse.saksnr().getText());
      }
    }

    redirectToSak(bisys.brukeroversikt());
    return Optional.empty();
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

  private boolean existingSakContainsSameBpBMAsRequest(
      String saksnr, Button linkToSak, SynthesizedBidragRequest request) {

    linkToSak.click();

    String fnrBp = bisys.sak().fnrBp().getText().replaceAll("\\s", "");
    String fnrBm = bisys.sak().fnrBm().getText().replaceAll("\\s", "");

    return fnrBp.equals(request.getFnrBp()) && fnrBm.equals(request.getFnrBm());
  }

  private boolean isBarnPresentInSak(String saksnr, SynthesizedBidragRequest request) {
    // Fill in saksnr
    bisys.sak().sokSaksnr().setValue(saksnr);

    // Click "Hent"
    bisys.sak().hentSak().click();

    List<Label> fnrBarnListe = bisys.sak().fnrBarn();

    for (Label fnrBarn : fnrBarnListe) {
      String fnr = fnrBarn.getText();
      if (fnr.equals(request.getFnrBa().replaceAll("\\s", ""))) return true;
    }
    return false;
  }

  private void createRollerAndGoToSoknad(
      Roller rollerPage, SynthesizedBidragRequest request, boolean ignoreExisingSakError) {

    List<RolleBarn> barnListe = rollerPage.barnListe();

    // Fill in FNR barn and samhandler
    Person barn = barnListe.get(0).person();
    Samhandler samhandler = barnListe.get(0).samhandler();

    barn.fnr().setValue(request.getFnrBa());
    samhandler.fnr().setValue(request.getFnrBa());

    // Add BP & BM FNRs
    rollerPage.rolleBp().person().fnr().setValue(request.getFnrBp());
    rollerPage.rolleBp().rolleUkjent().toggle(false);

    rollerPage.rolleBm().person().fnr().setValue(request.getFnrBm());
    rollerPage.rolleBm().rolleUkjent().toggle(false);

    debug(rollerPage);

    // Finalize Roller-view
    rollerPage.executeLagreOgNySoknad().click();

    debug(rollerPage);

    // If missing relation between parties, check ignore and re-submit
    try {
      rollerPage.ignorerRelasjonBarnogBMBP().toggle(true);
      rollerPage.executeLagreOgNySoknad().click();
      debug(rollerPage);
    } catch (ElementNotFoundException | NoSuchElementException ee) {
      ee.printStackTrace();
    }

    // Sak with same parties already exists
    try {
      if (!ignoreExisingSakError) {
        handleExistingSakError(rollerPage, request);
      }
      rollerPage.executeLagreOgNySoknad().click();
      debug(rollerPage);

    } catch (ElementNotFoundException | NoSuchElementException ee) {
      ee.printStackTrace();
    }
  }

  private void handleExistingSakError(Roller rollerPage, SynthesizedBidragRequest request) {
    List<Label> errors = rollerPage.errors();

    for (Label error : errors) {
      String errorMsg = error.getText();

      if (errorMsg.contains(Roller.ERROR_SAK_EKSISTERER_MED_SAMME_BM_OG_BP)) {
        String saksnrRegEx = "saksnr\\s\\d{7}";
        String saksnrDigitsOnlyRegEx = "\\d{7}";

        Pattern saksnrPattern = Pattern.compile(saksnrRegEx);
        Matcher saksnrMatch = saksnrPattern.matcher(errorMsg);

        if (saksnrMatch.find()) {
          String saksnr = errorMsg.substring(saksnrMatch.start(), saksnrMatch.end());
          Pattern saksnrDigitsOnly = Pattern.compile(saksnrDigitsOnlyRegEx);

          Matcher saksnrDigitsOnlyMatch = saksnrDigitsOnly.matcher(saksnr);
          saksnrDigitsOnlyMatch.find();

          saksnr = saksnr.substring(saksnrDigitsOnlyMatch.start());
          redirectToSak(rollerPage);

          if (isBarnPresentInSak(saksnr, request)) {
            getSakAndGoToSoknad(saksnr, request);
          } else {
            bisys.sak().nySak().click();
            createRollerAndGoToSoknad(rollerPage, request, true);
          }

          return;
        }
      }
    }
  }

  private void redirectToSak(BisysPage page) {
    page.header().oppgavelister().click();
    bisys.oppgaveliste().header().velgSkjermbilde().select("Sak");
  }

  private static boolean sakActive(String status) {
    if ((status.equals(Brukeroversikt.KODE_BIDR_STATUS_INAKTIV_DEKODE)
        || status.equals(Brukeroversikt.KODE_BIDR_STATUS_SANERT_DEKODE))) {
      return false;
    } else {
      return true;
    }
  }

  private static class BisysBrowserConfigurer implements BrowserConfigurer {

    @Override
    public void configure(WebClient client) {
      client.getOptions().setThrowExceptionOnScriptError(false);
    }
  }
}
