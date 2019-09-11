package no.nav.registre.bisys.consumer.ui.sak;

import static no.nav.registre.bisys.consumer.ui.BisysUiSupport.getSak;
import static no.nav.registre.bisys.consumer.ui.BisysUiSupport.redirectToSak;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import lombok.extern.slf4j.Slf4j;
import net.morher.ui.connect.api.element.Label;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.BisysPageTitle;
import no.nav.bidrag.ui.bisys.rolle.Person;
import no.nav.bidrag.ui.bisys.rolle.RolleBarn;
import no.nav.bidrag.ui.bisys.rolle.Roller;
import no.nav.bidrag.ui.bisys.rolle.Samhandler;
import no.nav.bidrag.ui.bisys.soknad.request.SoknadRequest;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;

@Component
@Slf4j
public class BisysUiRollerConsumer {

    private static final String BARN_NOT_ADDED_TO_SAK = "Barn with id %s was not added to existing sak %s";
    private static final String DUPLIKAT_SAK = "Det finnes allerede en sak med samme BM og BP på saksnr ";
    private static final String ERROR_BARN_MANGLER_RELASJON_TIL_BM_BP_REGEX = "Barn med fødselsnummer \\d{11} mangler relasjon til angitt BM/BP";

    /**
     * Creates roller for Sak
     * 
     * <code>
     *  - Expected entry page: Roller
     *  - Expected exit page: Sak
     * </code>
     * 
     * @param bisys
     * @param request
     * @param ignoreExistingSakError
     * @return
     * @throws BidragRequestProcessingException
     */
    public BisysPageTitle createRoller(BisysApplication bisys, SoknadRequest request,
            boolean ignoreExistingSakError) throws BidragRequestProcessingException {

        BisysPageTitle activePageRef = BisysUiSupport.checkCorrectActivePage(bisys, BisysPageTitle.ROLLER);
        Roller rollerPage = (Roller) BisysUiSupport.getActiveBisysPage(bisys);

        try {
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
            handleMissingRelations(rollerPage);

            // Sak with same parties already exists
            handleExistanceOfSakWithSameBmBp(bisys, ignoreExistingSakError);

            activePageRef = BisysUiSupport.getBisysPageReference(bisys);

            if (activePageRef.equals(BisysPageTitle.ROLLER)) {
                String saksnr = rollerPage.saksnr().getText();
                if (saksnr != null && !saksnr.isEmpty()) {
                    // Go to Sak
                    rollerPage.tilbake().click();
                } else {
                    throw new BidragRequestProcessingException("Saksnr missing from Roller!", bisys.bisysPage());
                }
            } else if (activePageRef.equals(BisysPageTitle.SAK)) {
                verifySaksnrPresentOnPage(bisys);
            } else {
                activePageRef = BisysUiSupport.checkCorrectActivePage(bisys, BisysPageTitle.SAK);
            }
            return activePageRef;
        } catch (ElementNotFoundException | NoSuchElementException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(), e);
        }
    }

    private void handleMissingRelations(Roller rollerPage) {
        try {
            rollerPage.ignorerRelasjonBarnogBMBP().toggle(true);
            rollerPage.executeLagre().click();
        } catch (ElementNotFoundException | NoSuchElementException ee) {
            log.info("IgnorerRelasjonBarnOgBmBp-element not found.");
        }
    }

    private void handleExistanceOfSakWithSameBmBp(BisysApplication bisys, boolean ignoreExistingSakError) throws BidragRequestProcessingException {
        try {
            if (!ignoreExistingSakError) {
                handleExistingSakError(bisys);
            }
        } catch (ElementNotFoundException | NoSuchElementException ee) {
            log.info("Sak with same parties does not already exist.");
        }
    }

    private void verifySaksnrPresentOnPage(BisysApplication bisys) throws BidragRequestProcessingException {
        try {
            bisys.sak().saksnr().getText();
        } catch (ElementNotFoundException | NoSuchElementException e) {
            throw new BidragRequestProcessingException("Saksnr missing from Sak!", bisys.bisysPage(), e);
        }
    }

    /**
     * Adds barn to existing sak
     * 
     * <code>
     *  - Expected entry page: Roller
     *  - Expected exit page: Roller
     * </code>
     * 
     * @param bisys
     * @param fnrBa
     * @return ActiveBisysPage if activePage equals Roller on exits
     * @throws BidragRequestProcessingException
     */
    public BisysPageTitle addBarnToSak(BisysApplication bisys, String fnrBa)
            throws BidragRequestProcessingException {

        Roller roller = (Roller) BisysUiSupport.getActiveBisysPage(bisys);

        roller.leggeTilLinje().click();

        RolleBarn nyttBarn = roller.barnListe().get(0);
        String saksnr = roller.saksnr().getText();

        nyttBarn.person().fnr().setValue(fnrBa);
        roller.executeLagre().click();

        BisysPageTitle activePageRef = BisysUiSupport.getBisysPageReference(bisys);
        List<Label> errors = bisys.bisysPage().errors();
        if (activePageRef.equals(BisysPageTitle.ROLLER) && !errors.isEmpty()) {
            for (Label error : errors) {
                if (error.getText().matches(ERROR_BARN_MANGLER_RELASJON_TIL_BM_BP_REGEX)) {
                    log.info(error.getText());

                    roller.ignorerRelasjonBarnogBMBP().toggle();
                    roller.executeLagre().click();
                    if (barnIsIncluded(roller.barnListe(), fnrBa)) {
                        if (activePageRef.equals(BisysPageTitle.ROLLER)) {
                            return activePageRef;
                        }
                    } else {
                        throw new BidragRequestProcessingException(bisys.bisysPage(),
                                new Exception(String.format(BARN_NOT_ADDED_TO_SAK, fnrBa, saksnr)));
                    }
                } else if (error.getText().trim().length() > 0) {
                    throw new BidragRequestProcessingException(bisys.bisysPage(),
                            new Exception(String.format(BARN_NOT_ADDED_TO_SAK, fnrBa, saksnr)));
                }
            }
        } else {
            activePageRef = BisysUiSupport.getBisysPageReference(bisys);
            if (activePageRef.equals(BisysPageTitle.ROLLER)) {
                return activePageRef;
            }
        }

        activePageRef = BisysUiSupport.getBisysPageReference(bisys);
        if (activePageRef.equals(BisysPageTitle.ROLLER)) {
            return activePageRef;
        }

        throw new BidragRequestProcessingException(bisys.bisysPage(),
                new Exception(String.format(BARN_NOT_ADDED_TO_SAK, fnrBa, saksnr)));
    }

    public boolean barnIsIncluded(List<RolleBarn> list, String fnrBa) {
        for (RolleBarn barn : list) {
            if (barn.person().fnr().getValue().equals(fnrBa)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if an existing sak error is displayed to user. If present, open existing sak.
     * 
     * <code>
     *  - Expected entry page: Roller
     *  - Expected exit page: 
     * </code>
     * 
     * @param bisys
     * @return
     * @throws BidragRequestProcessingException
     */
    private BisysPageTitle handleExistingSakError(BisysApplication bisys) throws BidragRequestProcessingException {

        BisysUiSupport.checkCorrectActivePage(bisys, BisysPageTitle.ROLLER);

        List<Label> errors = bisys.bisysPage().errors();

        for (Label error : errors) {
            String errorMsg = error.getText();

            if (errorMsg.contains(DUPLIKAT_SAK)) {
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
                    redirectToSak(bisys);

                    BisysPageTitle activePageRef = BisysUiSupport.getBisysPageReference(bisys);
                    getSak(bisys, saksnr);

                    return activePageRef;
                }
            }
        }
        return BisysUiSupport.getBisysPageReference(bisys);
    }
}
