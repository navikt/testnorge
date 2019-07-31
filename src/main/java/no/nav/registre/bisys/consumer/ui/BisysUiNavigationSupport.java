package no.nav.registre.bisys.consumer.ui;

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
}
