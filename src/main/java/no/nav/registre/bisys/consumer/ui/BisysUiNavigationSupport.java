package no.nav.registre.bisys.consumer.ui;

import com.gargoylesoftware.htmlunit.WebClient;
import net.morher.ui.connect.api.ApplicationDefinition;
import net.morher.ui.connect.api.listener.ActionLogger;
import net.morher.ui.connect.html.HtmlMapper;
import net.morher.ui.connect.html.listener.WaitForJavaScriptListener;
import net.morher.ui.connect.http.Browser;
import net.morher.ui.connect.http.BrowserConfigurer;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.common.BisysPage;

public class BisysUiNavigationSupport {

  public static void getSak(BisysApplication bisys, String saksnr) {
    // Fill in saksnr
    bisys.sak().sokSaksnr().setValue(saksnr);

    // Click "Hent"
    bisys.sak().hentSak().click();
  }

  public static void redirectToSak(BisysApplication bisys, BisysPage page) {
    page.header().oppgavelister().click();
    bisys.oppgaveliste().header().velgSkjermbilde().select("Sak");
  }

  public static BisysApplication bisysLogon(
      String bisysUrl,
      String saksbehandlerUid,
      String saksbehandlerPwd,
      String rolleSaksbehandler,
      int enhet) {
    BisysApplication bisys = openBrowser(bisysUrl);
    bisys.openamLoginPage().signIn(saksbehandlerUid, saksbehandlerPwd);

    // TODO: Introdusere mapping mellom saksbehandlers NAV-kontor og synt.brukers tilknyttede enhet
    // Velg pålogget enhet
    bisys.velgGruppe().velgGruppe(rolleSaksbehandler);
    bisys.velgEnhet().velgEnhet(enhet);
    return bisys;
  }

  private static BisysApplication openBrowser(String url) {
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
