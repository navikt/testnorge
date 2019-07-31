package no.nav.registre.bisys.consumer.ui.modules;

import static no.nav.registre.bisys.consumer.ui.BisysUiNavigationSupport.getSak;
import static no.nav.registre.bisys.consumer.ui.BisysUiNavigationSupport.redirectToSak;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.morher.ui.connect.api.element.Button;
import no.nav.bidrag.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.brukeroversikt.Brukeroversikt;
import no.nav.bidrag.ui.bisys.brukeroversikt.Ytelseslinje;
import no.nav.bidrag.ui.bisys.rolle.Roller;
import no.nav.bidrag.ui.bisys.sak.Sak;

@Slf4j
public class BisysUiSakConsumer {

  private BisysApplication bisys;

  @Autowired private BisysUiRollerConsumer rollerConsumer;

  public Sak openOrCreateSak(BisysApplication bisysApplication, SynthesizedBidragRequest request) {

    this.bisys = bisysApplication;

    Optional<String> existingSaksnr = findExistingSakInBrukeroversikt(request);

    if (existingSaksnr.isPresent()) {
      getSak(bisys, existingSaksnr.get());
    } else {
      bisysApplication.sak().nySak().click();
      Roller roller = rollerConsumer.createRoller(bisys, request, false);
      roller.tilbake().click();
    }

    return bisys.sak();
  }

  private Optional<String> findExistingSakInBrukeroversikt(SynthesizedBidragRequest request) {
    bisys.oppgaveliste().header().velgSkjermbilde().select("Brukeroversikt");

    bisys.brukeroversikt().fnr().setValue(request.getFnrBa());

    bisys.brukeroversikt().hent().click();

    List<Ytelseslinje> ytelser = bisys.brukeroversikt().ytelser();

    if (ytelser == null || ytelser.isEmpty()) {
      redirectToSak(bisys, bisys.brukeroversikt());
      return Optional.empty();
    }

    for (Ytelseslinje ytelse : ytelser) {
      String status;
      try {
        status = ytelse.status().getText();
      } catch (ElementNotFoundException | NoSuchElementException ee) {
        log.info("Ingen ytelser funnet på barn {}", request.getFnrBa());

        redirectToSak(bisys, bisys.brukeroversikt());
        return Optional.empty();
      }
      if (sakActive(status) && existingSakContainsSameBpBMAsRequest(ytelse.linkToSak(), request)) {

        redirectToSak(bisys, bisys.brukeroversikt());
        return Optional.of(ytelse.saksnr().getText());
      }
    }

    redirectToSak(bisys, bisys.brukeroversikt());
    return Optional.empty();
  }

  private boolean existingSakContainsSameBpBMAsRequest(
      Button linkToSak, SynthesizedBidragRequest request) {

    linkToSak.click();

    String fnrBp = bisys.sak().fnrBp().getText().replaceAll("\\s", "");
    String fnrBm = bisys.sak().fnrBm().getText().replaceAll("\\s", "");

    return fnrBp.equals(request.getFnrBp()) && fnrBm.equals(request.getFnrBm());
  }

  private static boolean sakActive(String status) {
    return !(Brukeroversikt.KODE_BIDR_STATUS_INAKTIV_DEKODE.equals(status)
        || Brukeroversikt.KODE_BIDR_STATUS_SANERT_DEKODE.equals(status));
  }
}
