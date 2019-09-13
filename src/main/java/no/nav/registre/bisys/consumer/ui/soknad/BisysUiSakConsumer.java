package no.nav.registre.bisys.consumer.ui.soknad;

import static no.nav.registre.bisys.consumer.ui.BisysUiSupport.getSak;
import static no.nav.registre.bisys.consumer.ui.BisysUiSupport.redirectToSak;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import lombok.extern.slf4j.Slf4j;
import net.morher.ui.connect.api.element.Button;
import net.morher.ui.connect.api.element.Label;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.BisysPageTitle;
import no.nav.bidrag.ui.bisys.brukeroversikt.Brukeroversikt;
import no.nav.bidrag.ui.bisys.brukeroversikt.Ytelseslinje;
import no.nav.bidrag.ui.bisys.sak.Sak;
import no.nav.bidrag.ui.bisys.soknad.request.SoknadRequest;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.exception.BidragRequestRuntimeException;

@Component
@Slf4j
public class BisysUiSakConsumer {

    @Autowired
    private BisysUiRollerConsumer rollerConsumer;

    /**
     * Opens existing or creates new Sak.
     * 
     * <code>
     *  - Expected entry page: Oppgaveliste
     *  - Expected exit page: Sak
     * </code>
     * 
     * @param bisys
     * @param request
     * @return
     * @throws BidragRequestProcessingException
     */
    public BisysPageTitle openOrCreateSak(BisysApplication bisys, SoknadRequest request)
            throws BidragRequestProcessingException {

        BisysPageTitle activePage = BisysUiSupport.checkCorrectActivePage(bisys, BisysPageTitle.OPPGAVELISTE);

        try {

            Optional<String> existingSaksnr = findExistingSakInBrukeroversikt(bisys, request);
            activePage = BisysUiSupport.getBisysPageReference(bisys);
            Sak sak = (Sak) BisysUiSupport.getActiveBisysPage(bisys);

            if (existingSaksnr.isPresent()) {
                getSak(bisys, existingSaksnr.get());
            } else {
                sak.nySak().click();
                activePage = rollerConsumer.createRoller(bisys, request, false);
            }

            return activePage;
        } catch (ElementNotFoundException | NoSuchElementException e) {
            throw new BidragRequestRuntimeException(bisys.bisysPage(), e);
        }
    }

    /**
     * 
     * @param bisys
     * @param request
     * @return
     * @throws BidragRequestProcessingException
     */
    private final Optional<String> findExistingSakInBrukeroversikt(BisysApplication bisys,
            SoknadRequest request)
            throws BidragRequestProcessingException {

        bisys.bisysPage().header().velgSkjermbilde().select(BisysPageTitle.BRUKEROVERSIKT.pageName());

        Brukeroversikt brukeroversikt = (Brukeroversikt) BisysUiSupport.getActiveBisysPage(bisys);

        brukeroversikt.fnr().setValue(request.getFnrBm());
        brukeroversikt.hent().click();
        List<Ytelseslinje> ytelser = brukeroversikt.ytelser();

        if (ytelser == null || ytelser.isEmpty()) {
            redirectToSak(bisys);
            return Optional.empty();
        }

        for (Ytelseslinje ytelse : ytelser) {
            String status;
            try {
                status = ytelse.status().getText();

            } catch (ElementNotFoundException | NoSuchElementException ee) {
                log.info("Ingen ytelser funnet på bm {}", request.getFnrBm());
                redirectToSak(bisys);

                return Optional.empty();
            }
            if (sakActive(status)
                    && existingSakContainsSameBpBmAsRequest(bisys, ytelse.linkToSak(), request)) {
                return Optional.of(ytelse.saksnr().getText());
            }
        }

        redirectToSak(bisys);

        return Optional.empty();
    }

    /**
     * 
     * <code>
     *  - Expected entry page: Brukeroversikt or Sak
     *  - Expected exit page: Sak
     * </code>
     * 
     * 
     * @param bisys
     * @param linkToSak
     * @param request
     * @return true if matching Sak is found, return false if not
     * @throws BidragRequestProcessingException
     */
    private final boolean existingSakContainsSameBpBmAsRequest(BisysApplication bisys,
            Button linkToSak, SoknadRequest request)
            throws BidragRequestProcessingException {

        BisysPageTitle activePageRef = BisysUiSupport.getBisysPageReference(bisys);

        if (activePageRef.equals(BisysPageTitle.BRUKEROVERSIKT)) {
            linkToSak.click();
        } else {
            BisysUiSupport.checkCorrectActivePage(bisys, BisysPageTitle.SAK);
        }

        Sak sak = (Sak) BisysUiSupport.getActiveBisysPage(bisys);

        Optional<String> fnrBpBisys;

        try {
            Label fnrBp = sak.fnrBp();
            fnrBpBisys = Optional.ofNullable(fnrBp.getText());
        } catch (NoSuchElementException | ElementNotFoundException e) {
            fnrBpBisys = Optional.empty();
        }

        Optional<String> fnrBpRequest = Optional.ofNullable(request.getFnrBp());
        Boolean fnrBpMatches = fnrBpRequest.isPresent() && fnrBpBisys.isPresent()
                && fnrBpRequest.get().replaceAll("\\s", "").equals(fnrBpBisys.get().replaceAll("\\s", ""));

        String fnrBmBisys = sak.fnrBm().getText().replaceAll("\\s", "");
        Boolean fnrBmMatches = fnrBmBisys.equals(request.getFnrBm());

        return fnrBmMatches && fnrBpMatches;

    }

    private static final boolean sakActive(String status) {
        return !(Brukeroversikt.KODE_BIDR_STATUS_INAKTIV_DEKODE.equals(status)
                || Brukeroversikt.KODE_BIDR_STATUS_SANERT_DEKODE.equals(status));
    }
}
