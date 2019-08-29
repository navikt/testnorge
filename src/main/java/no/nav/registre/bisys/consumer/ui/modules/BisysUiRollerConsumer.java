package no.nav.registre.bisys.consumer.ui.modules;

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
import no.nav.bidrag.ui.bisys.BisysApplication.ActiveBisysPage;
import no.nav.bidrag.ui.bisys.rolle.Person;
import no.nav.bidrag.ui.bisys.rolle.RolleBarn;
import no.nav.bidrag.ui.bisys.rolle.Roller;
import no.nav.bidrag.ui.bisys.rolle.Samhandler;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;

@Component
@Slf4j
public class BisysUiRollerConsumer {

    private static final String BARN_NOT_ADDED_TO_SAK = "Barn with id %s was not added to existing sak %s";

    private final static String ERROR_BARN_MANGLER_RELASJON_TIL_BM_BP = "Barn med fødselsnummer %s mangler relasjon til angitt BM/BP";

    private final static String DUPLIKAT_SAK = "Det finnes allerede en sak med samme BM og BP på saksnr ";

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
    public ActiveBisysPage createRoller(BisysApplication bisys, SynthesizedBidragRequest request,
            boolean ignoreExistingSakError) throws BidragRequestProcessingException {

        ActiveBisysPage activePage = BisysUiSupport.checkCorrectActivePage(bisys, ActiveBisysPage.ROLLER);
        Roller rollerPage = (Roller) bisys.getActivePage(activePage);

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
            try {
                rollerPage.ignorerRelasjonBarnogBMBP().toggle(true);
                rollerPage.executeLagre().click();
            } catch (ElementNotFoundException | NoSuchElementException ee) {
                log.info("IgnorerRelasjonBarnOgBmBp-element not found.");
            }

            // Sak with same parties already exists
            try {
                if (!ignoreExistingSakError) {
                    handleExistingSakError(bisys, rollerPage, request);
                }
            } catch (ElementNotFoundException | NoSuchElementException ee) {
                log.info("Sak with same parties does not already exist.");
            }

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            if (activePage.equals(ActiveBisysPage.ROLLER)) {
                String saksnr = rollerPage.saksnr().getText();
                if (saksnr != null && !saksnr.isEmpty()) {
                    // Go to Sak
                    rollerPage.tilbake().click();
                } else {
                    throw new BidragRequestProcessingException("Saksnr missing from Roller!", activePage,
                            bisys.bisysPage());
                }
            } else if (activePage.equals(ActiveBisysPage.SAK)) {
                try {
                    bisys.sak().saksnr().getText();
                } catch (ElementNotFoundException | NoSuchElementException e) {
                    throw new BidragRequestProcessingException("Saksnr missing from Sak!", activePage,
                            bisys.bisysPage());
                }
            } else {
                activePage = BisysUiSupport.checkCorrectActivePage(bisys, ActiveBisysPage.SAK);
            }
            return activePage;
        } catch (ElementNotFoundException | NoSuchElementException e) {
            throw new BidragRequestProcessingException(activePage, bisys.bisysPage(), e);
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
    public ActiveBisysPage addBarnToSak(BisysApplication bisys, String fnrBa)
            throws BidragRequestProcessingException {

        ActiveBisysPage activePage = BisysUiSupport.checkCorrectActivePage(bisys, ActiveBisysPage.ROLLER);

        Roller roller = (Roller) bisys.getActivePage(activePage);

        roller.leggeTilLinje().click();
        List<RolleBarn> barn = roller.barnListe();
        String saksnr = roller.saksnr().getText();

        for (RolleBarn rolleBarn : barn) {
            try {
                rolleBarn.person().fnr().setValue(fnrBa);
                roller.executeLagre().click();

                activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();
                List<Label> errors = bisys.bisysPage().errors();
                if (activePage.equals(ActiveBisysPage.ROLLER) && errors.size() > 0) {
                    for (Label error : errors) {
                        if (error.getText()
                                .equals(String.format(ERROR_BARN_MANGLER_RELASJON_TIL_BM_BP, fnrBa))) {
                            roller.ignorerRelasjonBarnogBMBP().toggle();
                            roller.executeLagre().click();
                            if (barnIsIncluded(roller.barnListe(), fnrBa)) {
                                if (activePage.equals(ActiveBisysPage.ROLLER)) {
                                    return activePage;
                                }
                            } else {
                                throw new BidragRequestProcessingException(activePage, bisys.bisysPage(),
                                        new Exception(String.format(BARN_NOT_ADDED_TO_SAK, fnrBa, saksnr)));
                            }
                        }
                    }
                } else {
                    activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();
                    if (activePage.equals(ActiveBisysPage.ROLLER)) {
                        return activePage;
                    }
                }

            } catch (ElementNotFoundException | NoSuchElementException e) {
                log.info("TextField fnr not found.");
            }
        }

        activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();
        if (activePage.equals(ActiveBisysPage.ROLLER)) {
            return activePage;
        }

        throw new BidragRequestProcessingException(activePage, bisys.bisysPage(),
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
     * @param rollerPage
     * @param request
     * @return
     * @throws BidragRequestProcessingException
     */
    private ActiveBisysPage handleExistingSakError(BisysApplication bisys, Roller rollerPage,
            SynthesizedBidragRequest request) throws BidragRequestProcessingException {

        ActiveBisysPage activePage = BisysUiSupport.checkCorrectActivePage(bisys, ActiveBisysPage.ROLLER);

        List<Label> errors = bisys.bisysPage().errors();

        for (Label error : errors) {
            String errorMsg = error.getText();

            if (errorMsg.contains(String.format(DUPLIKAT_SAK))) {
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

                    activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();
                    getSak(bisys, saksnr);

                    return activePage;
                }
            }
        }
        return activePage;
    }
}
