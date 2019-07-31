package no.nav.registre.bisys.consumer.ui.modules;

import static no.nav.registre.bisys.consumer.ui.BisysUiNavigationSupport.getSak;
import static no.nav.registre.bisys.consumer.ui.BisysUiNavigationSupport.redirectToSak;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.morher.ui.connect.api.element.Label;
import no.nav.bidrag.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.rolle.Person;
import no.nav.bidrag.ui.bisys.rolle.RolleBarn;
import no.nav.bidrag.ui.bisys.rolle.Roller;
import no.nav.bidrag.ui.bisys.rolle.Samhandler;

@Slf4j
public class BisysUiRollerConsumer {

  private BisysApplication bisys;

  public Roller createRoller(
      BisysApplication bisys, SynthesizedBidragRequest request, boolean ignoreExisingSakError) {

    this.bisys = bisys;

    Roller rollerPage = bisys.roller();

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

    // Finalize Roller-view
    rollerPage.executeLagre().click();

    // If missing relation between parties, check ignore and re-submit
    try {
      rollerPage.ignorerRelasjonBarnogBMBP().toggle(true);
      rollerPage.executeLagre().click();
    } catch (ElementNotFoundException | NoSuchElementException ee) {
      log.info("IgnorerRelasjonBarnOgBmBp-element not found.");
    }

    // Sak with same parties already exists
    try {
      if (!ignoreExisingSakError) {
        handleExistingSakError(rollerPage, request);
      }
      rollerPage.executeLagre().click();

    } catch (ElementNotFoundException | NoSuchElementException ee) {
      log.info("Sak with same parties does not already exist.");
    }

    return rollerPage;
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
          redirectToSak(bisys, rollerPage);

          if (isBarnPresentInSak(saksnr, request)) {
            getSak(bisys, saksnr);
          } else {
            bisys.sak().nySak().click();
            createRoller(bisys, request, true);
          }
          return;
        }
      }
    }
  }

  private boolean isBarnPresentInSak(String saksnr, SynthesizedBidragRequest request) {
    // Fill in saksnr
    bisys.sak().sokSaksnr().setValue(saksnr);

    // Click "Hent"
    bisys.sak().hentSak().click();

    List<Label> fnrBarnListe = bisys.sak().fnrBarn();

    for (Label fnrBarn : fnrBarnListe) {
      String fnr = fnrBarn.getText();
      if (fnr.equals(request.getFnrBa().replaceAll("\\s", ""))) {
        return true;
      }
    }
    return false;
  }
}
