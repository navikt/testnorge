package no.nav.registre.bisys.consumer.ui.sak;

import static no.nav.registre.bisys.config.AppConfig.STANDARD_DATE_FORMAT_BISYS;
import static no.nav.registre.bisys.config.AppConfig.STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST;

import java.util.List;
import java.util.NoSuchElementException;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.ActiveBisysPage;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknFraConstants;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknTypeConstants;
import no.nav.bidrag.ui.bisys.rolle.Roller;
import no.nav.bidrag.ui.bisys.sak.Sak;
import no.nav.bidrag.ui.bisys.sak.UnderBehandling;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.bidrag.ui.bisys.soknad.Soknadslinje;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;

@Slf4j
@Component
public class BisysUiSoknadConsumer {

    @Autowired
    private BisysUiSakConsumer sakConsumer;

    @Autowired
    private BisysUiRollerConsumer rollerConsumer;

    /**
     * Opens exsisting or creates new søknad in new or existing sak.
     * 
     * <code>
     *  - Expected entry page: Oppgaveliste 
     *  - Expected exit page: Soknad
     * </code>
     * 
     * @param request
     * @param bisys
     * @throws BidragRequestProcessingException
     */
    public ActiveBisysPage openOrCreateSoknad(BisysApplication bisys,
            SynthesizedBidragRequest request) throws BidragRequestProcessingException {

        try {
            sakConsumer.openOrCreateSak(bisys, request);
            ensureBarnIsPartInSak(bisys, request.getFnrBa());
            return openExistingOrCreateNewSoknad(bisys, request);

        } catch (ElementNotFoundException | NoSuchElementException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(), e);
        }
    }

    private void ensureBarnIsPartInSak(BisysApplication bisys, String fnrBa) throws BidragRequestProcessingException {

        BisysUiSupport.checkCorrectActivePage(bisys, ActiveBisysPage.SAK);

        bisys.bisysPage().sideBarMenu().roller().click();
        ActiveBisysPage activePage = ActiveBisysPage.getActivePage(bisys.bisysPage().header().tittel()).get();

        Roller roller = (Roller) bisys.getActivePage(activePage);

        if (!rollerConsumer.barnIsIncluded(roller.barnListe(), fnrBa)) {
            rollerConsumer.addBarnToSak(bisys, fnrBa);
        }
        bisys.bisysPage().sideBarMenu().tilbake().click();
    }

    /**
     * 
     * <code>
     *  - Expected page on entry: Sak 
     *  - Expected page on exit: Soknad 
     * </code>
     * 
     * @param bisys
     * @param request
     * @return activeBisysPage on exit
     * @throws BidragRequestProcessingException
     */
    private ActiveBisysPage openExistingOrCreateNewSoknad(BisysApplication bisys,
            SynthesizedBidragRequest request)
            throws BidragRequestProcessingException {

        ActiveBisysPage activePage = BisysUiSupport.checkCorrectActivePage(bisys, ActiveBisysPage.SAK);

        Sak sak = (Sak) bisys.getActivePage(activePage);

        for (UnderBehandling soknadUnderBehandling : sak.soknaderUnderBehandling()) {
            try {
                soknadUnderBehandling.mottattdato().getText();
            } catch (ElementNotFoundException | NoSuchElementException e) {
                log.info("No exsisting søknader in progress for saksnr {}. Creating new søknad.",
                        sak.saksnr().getText());
                break;
            }

            String soknGrKomKodeRequest = request.getSoktOm();
            String soknGrKomDekodeRequest = KodeSoknGrKomConstants.dekodeMap().get(soknGrKomKodeRequest);
            String soknTypeKodeRequest = request.getSoknadstype();
            String soknTypeDekodeRequest = KodeSoknTypeConstants.dekodeMap().get(soknTypeKodeRequest);

            LocalDate mottattdatoRequest = LocalDate.parse(request.getMottattDato(),
                    DateTimeFormat.forPattern(STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST));

            LocalDate mottattdatoBisys = LocalDate.parse(soknadUnderBehandling.mottattdato().getText(),
                    DateTimeFormat.forPattern(STANDARD_DATE_FORMAT_BISYS));

            String soknadFraBisys = soknadUnderBehandling.soknadFra().getText();
            String soknadFraDekodeRequest = KodeSoknFraConstants.dekodeMap().get(request.getSoknadFra());

            if (mottattdatoBisys.isEqual(mottattdatoRequest)
                    && soknadFraBisys.equals(soknadFraDekodeRequest)
                    && soknadUnderBehandling.soknadsgruppe().getText().equals(soknGrKomDekodeRequest)
                    && soknadUnderBehandling.typeSoknad().getText().equals(soknTypeDekodeRequest)) {

                log.info(
                        "Found existing søknad in sak. Søknad details: mottattdato: {}, soknadFra: {}, soknadsgruppe: {}, soknadstype: {}",
                        soknadUnderBehandling.mottattdato().getText(),
                        soknadUnderBehandling.soknadFra().getText(),
                        soknadUnderBehandling.soknadsgruppe().getText(),
                        soknadUnderBehandling.typeSoknad().getText());

                soknadUnderBehandling.velg().click();
                activePage = ActiveBisysPage.getActivePage(bisys.bisysPage().header().tittel()).get();

                Soknad soknad = (Soknad) bisys.getActivePage(activePage);
                if (requestedBaIncludedInSoknad(soknad, request.getFnrBa())) {
                    return activePage;
                }
            }
        }

        log.info("Existing søknad not found. Creating new søknad with mottattdato {}",
                request.getMottattDato());

        if (activePage.equals(ActiveBisysPage.SOKNAD)) {
            bisys.bisysPage().sideBarMenu().sak().click();
        }

        return createNewSoknad(bisys, request);
    }

    private boolean requestedBaIncludedInSoknad(Soknad soknad, String fnrBaRequest) {
        List<Soknadslinje> soknadslinjer = soknad.soknadslinjer();
        for (Soknadslinje soknadslinje : soknadslinjer) {
            String fnrBaSoknad = soknadslinje.fnr().getText().replaceAll("\\s", "");
            if (fnrBaSoknad.equals(fnrBaRequest)) {
                return true;
            }
        }
        return false;

    }

    private ActiveBisysPage createNewSoknad(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {

        ActiveBisysPage activePage = BisysUiSupport.checkCorrectActivePage(bisys, ActiveBisysPage.SAK);

        Sak sak = (Sak) bisys.getActivePage(activePage);
        try {
            sak.nySoknad().click();
        } catch (NoSuchElementException | ElementNotFoundException e) {
            throw new BidragRequestProcessingException("Soknad-button not visible. Check logged on enhet", sak, e);
        }

        activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();
        Soknad soknad = (Soknad) bisys.getActivePage(activePage);

        soknad.fillInAndSaveSoknad(request);

        log.info("### Søknad created ### application for barn {} was created.", request.getFnrBa());
        return activePage;

    }

}
