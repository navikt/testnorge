package no.nav.registre.bisys.consumer.ui;

import java.util.NoSuchElementException;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import lombok.AllArgsConstructor;
import net.morher.ui.connect.api.ApplicationDefinition;
import net.morher.ui.connect.api.listener.ActionLogger;
import net.morher.ui.connect.html.HtmlMapper;
import net.morher.ui.connect.html.listener.WaitForJavaScriptListener;
import net.morher.ui.connect.http.Browser;
import net.morher.ui.connect.http.BrowserConfigurer;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.ActiveBisysPage;
import no.nav.bidrag.ui.bisys.common.HeaderView;
import no.nav.bidrag.ui.bisys.sak.Sak;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.bidrag.ui.exception.BidragRequestRuntimeException;

@AllArgsConstructor
public class BisysUiSupport {

  String saksbehandlerUid;
  String saksbehandlerPwd;
  String bisysUrl;
  String rolleSaksbehandler;
  int enhet;

  public BisysApplication logon() throws BidragRequestProcessingException {
    return bisysLogon(bisysUrl, saksbehandlerUid, saksbehandlerPwd, rolleSaksbehandler, enhet);
  }

  /**
   * Opens Sak
   * 
   * <code>
   *  - Expected entry page: Sak
   *  - Expected exit page: Sak
   * </code>
   * 
   * @param bisys
   * @param saksnr
   * @throws BidragRequestProcessingException
   */
  public static void getSak(BisysApplication bisys, String saksnr)
      throws BidragRequestProcessingException {

    ActiveBisysPage activePage = checkCorrectActivePage(bisys, ActiveBisysPage.SAK);
    Sak sak = (Sak) bisys.getActivePage(activePage);

    // Fill in saksnr
    sak.sokSaksnr().setValue(saksnr);

    // Click "Hent"
    sak.hentSak().click();
  }

  /**
   * 
   * <code>
   *  - Expected entry page: Any page that contains header with link to Oppgaveliste 
   *  - Expected exit page: Sak
   * </code>
   * 
   * @param bisys
   * @throws BidragRequestProcessingException
   */
  public static void redirectToSak(BisysApplication bisys) throws BidragRequestProcessingException {

    ActiveBisysPage activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

    try {

      HeaderView header = bisys.bisysPage().header();
      header.oppgavelister().click();
      bisys.bisysPage().header().velgSkjermbilde().select("Sak");
      activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

    } catch (ElementNotFoundException | NoSuchElementException e) {
      throw new BidragRequestRuntimeException(activePage, bisys.bisysPage(), e);
    }
  }

  /**
   * Logs on to Bisys.
   * 
   * @param bisysUrl
   * @param saksbehandlerUid
   * @param saksbehandlerPwd
   * @param rolleSaksbehandler
   * @param enhet
   * @return bisysApplication
   * @throws BidragRequestProcessingException
   */
  public static BisysApplication bisysLogon(String bisysUrl, String saksbehandlerUid,
      String saksbehandlerPwd, String rolleSaksbehandler, int enhet)
      throws BidragRequestProcessingException {

    BisysApplication bisys = openBrowser(bisysUrl);
    ActiveBisysPage activePage = ActiveBisysPage.OPENAM_LOGIN_PAGE;

    try {
      bisys.openamLoginPage().signIn(saksbehandlerUid, saksbehandlerPwd);

      // TODO: Introdusere mapping mellom saksbehandlers NAV-kontor og synt.brukers tilknyttede enhet
      // Velg pålogget enhet
      bisys.velgGruppe().velgGruppe(rolleSaksbehandler);

      activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

      bisys.velgEnhet().velgEnhet(enhet);

      activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();
      return bisys;
    } catch (ElementNotFoundException | NoSuchElementException e) {
      throw new BidragRequestRuntimeException(activePage, bisys.bisysPage(), e);
    }
  }

  private static BisysApplication openBrowser(String url) {
    return ApplicationDefinition.of(BisysApplication.class).mapWith(new HtmlMapper())
        .addListener(new WaitForJavaScriptListener()).addListener(new ActionLogger())
        .connect(new Browser().asChrome().useInsecureSSL()
            .addConfigurer(new BisysBrowserConfigurer()).openUrl(url));
  }

  private static class BisysBrowserConfigurer implements BrowserConfigurer {

    @Override
    public void configure(WebClient client) {
      client.getOptions().setThrowExceptionOnScriptError(false);
    }
  }

  public static ActiveBisysPage checkCorrectActivePage(BisysApplication bisys,
      ActiveBisysPage expectedEntryPage) throws BidragRequestProcessingException {
    ActiveBisysPage activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

    if (!activePage.equals(expectedEntryPage)) {
      throw new BidragRequestProcessingException(activePage, bisys.bisysPage(),
          new Exception(BisysUiConsumer.INCORRECT_ENTRY_PAGE));
    } else {
      return activePage;
    }
  }
}
