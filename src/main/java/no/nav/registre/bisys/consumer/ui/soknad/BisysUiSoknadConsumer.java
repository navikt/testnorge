package no.nav.registre.bisys.consumer.ui.soknad;

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
import no.nav.bidrag.ui.bisys.BisysApplication.BisysPageTitle;
import no.nav.bidrag.ui.bisys.exception.BisysUiConnectException;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknFraConstants;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknTypeConstants;
import no.nav.bidrag.ui.bisys.rolle.Roller;
import no.nav.bidrag.ui.bisys.sak.Sak;
import no.nav.bidrag.ui.bisys.sak.UnderBehandling;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.bidrag.ui.bisys.soknad.Soknadslinje;
import no.nav.bidrag.ui.bisys.soknad.request.SoknadRequest;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;

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
    public BisysPageTitle openOrCreateSoknad(BisysApplication bisys,
            SoknadRequest request) throws BidragRequestProcessingException {

        try {
            sakConsumer.openOrCreateSak(bisys, request);
            ensureBarnIsPartInSak(bisys, request.getFnrBa());
            return openExistingOrCreateNewSoknad(bisys, request);

        } catch (ElementNotFoundException | NoSuchElementException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(), e);
        }
    }

    private void ensureBarnIsPartInSak(BisysApplication bisys, String fnrBa) throws BidragRequestProcessingException {

        BisysUiSupport.checkCorrectActivePage(bisys, BisysPageTitle.SAK);

        bisys.bisysPage().sideBarMenu().roller().click();

        Roller roller = (Roller) BisysUiSupport.getActiveBisysPage(bisys);

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
    private BisysPageTitle openExistingOrCreateNewSoknad(BisysApplication bisys,
            SoknadRequest request)
            throws BidragRequestProcessingException {

        BisysPageTitle activePage = BisysUiSupport.checkCorrectActivePage(bisys, BisysPageTitle.SAK);

        Sak sak = (Sak) BisysUiSupport.getActiveBisysPage(bisys);

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
                activePage = BisysPageTitle.getPageRef(bisys.bisysPage().header().tittel()).get();

                Soknad soknad = (Soknad) BisysUiSupport.getActiveBisysPage(bisys);
                if (requestedBaIncludedInSoknad(soknad, request.getFnrBa())) {
                    return activePage;
                }
            }
        }

        log.info("Existing søknad not found. Creating new søknad with mottattdato {}",
                request.getMottattDato());

        if (activePage.equals(BisysPageTitle.SOKNAD)) {
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

    private BisysPageTitle createNewSoknad(BisysApplication bisys, SoknadRequest request) throws BidragRequestProcessingException {

        Sak sak = (Sak) BisysUiSupport.getActiveBisysPage(bisys);
        try {
            sak.nySoknad().click();
        } catch (NoSuchElementException | ElementNotFoundException e) {
            throw new BidragRequestProcessingException("Soknad-button not visible. Check logged on enhet", sak, e);
        }

        Soknad soknad = (Soknad) BisysUiSupport.getActiveBisysPage(bisys);

        fillInAndSaveSoknad(soknad, request);

        log.info("### Søknad created ### application for barn {} was created.", request.getFnrBa());

        return BisysUiSupport.getBisysPageReference(bisys);

    }

    private void fillInAndSaveSoknad(Soknad soknad, SoknadRequest request) throws BidragRequestProcessingException {

        soknad.selectSoknadstype(request.getSoknadstype());

        try {
            soknad.selectSoknadFra(request.getSoknadFra());
            soknad.setFomDato(request.getSoktOm(), request.getSoktFra());
            soknad.setSoktOm(request.getFnrBa(), request.getSoktOm());
        } catch (BisysUiConnectException e) {
            throw new BidragRequestProcessingException(soknad, e);
        }

        soknad.setMottattDato(request.getMottattDato());

        try {
            soknad.selectFastsattI(request.getFastsattI());
        } catch (Exception e) {
            log.info("Element fastsattI eksisterer ikke.");
        }

        try {
            soknad.lagre().click();
            soknad.selectGebyrfritakBp(request.getGebyrfritakBp());
            soknad.selectGebyrfritakBm(request.getGebyrfritakBm());

            // Single soknad, one child per bidragsmelding.
            soknad.soknadslinjer().get(0).setInnbetalt(request.getInnbetalt());
            soknad.lagre().click();

        } catch (Exception e) {
            throw new BidragRequestProcessingException(soknad, e);
        }
    }

}
