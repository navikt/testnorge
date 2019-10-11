package no.nav.registre.bisys.consumer.ui.vedtak.types;

import static no.nav.registre.bisys.consumer.ui.BisysUiSupport.feedbackMatchFound;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.BisysPageTitle;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Forskuddsberegning;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.FatteVedtak;
import no.nav.registre.bisys.consumer.rs.request.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.vedtak.BisysUiYtelseberegningConsumer;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;

@Component
public class ForskuddConsumer {

    @Autowired
    private BisysUiYtelseberegningConsumer ytelsebereging;

    private static final String FEEDBACK_PERIODE_UTEN_INNTEKTER_REGEX = "(.*)PERIODE UTEN INNTEKTER(.*)";
    private static final String FEEDBACK_FORSKUDDSBEREGNING_ARSAK_MA_VARE_UTFYLT = "(.*)Årsak må være utfylt(.*)";
    private static final String FEEDBACK_FORSDKUDDSBEREGNING_UGYLDIG_FORSORGERKODE = "(.*)UGYLDIG FORSØRGER KODE(.*)";
    private static final String FEEDBACK_FORSKUDDSBEREGNING_OPPDATERING_OK = "(.*)OPPDATERING OK(.*)";

    public void runForskudd(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {

        lagreOgForskudd(bisys);

        BisysPageTitle title = BisysUiSupport.getBisysPageReference(bisys);

        // Ved opphør av forskudd
        if (BisysPageTitle.FORSKUDDSBEREGNING.equals(title)) {
            Forskuddsberegning forskuddsberegning = (Forskuddsberegning) BisysUiSupport.getActiveBisysPage(bisys);
            forskuddsberegning.lagreOgBeregn().click();
            if (feedbackMatchFound(FEEDBACK_PERIODE_UTEN_INNTEKTER_REGEX, bisys.bisysPage().errors())) {
                bisys.bisysPage().sideBarMenu().inntekter().click();
                ytelsebereging.fulfillInntekterAndBoforhold(bisys, request);
            }
        } else {
            ytelsebereging.fulfillInntekterAndBoforhold(bisys, request);
        }

        beregnForskuddOgFatteVedtak(bisys, request);
    }

    private void beregnForskuddOgFatteVedtak(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {
        try {
            Forskuddsberegning forskuddsberegning = (Forskuddsberegning) BisysUiSupport.getActiveBisysPage(bisys);
            forskuddsberegning.lagreOgBeregn().click();

            while (inputRequired(bisys)) {
                handleFeeback(bisys, request);
            }

            forskuddEndGame(bisys, forskuddsberegning);

        } catch (ElementNotFoundException | NoSuchElementException | ClassCastException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(), e);
        }
    }

    private boolean inputRequired(BisysApplication bisys) throws BidragRequestProcessingException {

        boolean feedbackMatches = feedbackMatchFound(FEEDBACK_FORSKUDDSBEREGNING_OPPDATERING_OK, bisys.bisysPage().messages());
        boolean currentPageIsForskuddsberegning = BisysPageTitle.FORSKUDDSBEREGNING.equals(BisysUiSupport.getBisysPageReference(bisys));
        boolean noErrors = bisys.bisysPage().errors().isEmpty();

        return !feedbackMatches
                || currentPageIsForskuddsberegning
                        && !noErrors;

    }

    private void forskuddEndGame(BisysApplication bisys, Forskuddsberegning forskuddsberegning) throws BidragRequestProcessingException {
        forskuddsberegning.lagreBeregnFatteVedtak().click();

        FatteVedtak fatteVedtak = (FatteVedtak) BisysUiSupport.getActiveBisysPage(bisys);
        fatteVedtak.executeFatteVedtak().click();
    }

    private void handleFeeback(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {

        Forskuddsberegning forskuddsberegning = (Forskuddsberegning) BisysUiSupport.getActiveBisysPage(bisys);

        setFieldOnError(bisys, request, forskuddsberegning);
        forskuddsberegning.lagreOgBeregn().click();

        if (BisysPageTitle.FORSKUDDSBEREGNING.equals(BisysUiSupport.getBisysPageReference(bisys)) && !bisys.bisysPage().errors().isEmpty()) {
            setFieldOnError(bisys, request, forskuddsberegning);
            forskuddsberegning.lagreOgBeregn().click();
        }
    }

    private void setFieldOnError(BisysApplication bisys, SynthesizedBidragRequest request, Forskuddsberegning forskuddsberegning) {
        if (feedbackMatchFound(FEEDBACK_FORSKUDDSBEREGNING_ARSAK_MA_VARE_UTFYLT, bisys.bisysPage().errors())) {
            forskuddsberegning.selectKodeArsak(request.getKodeVirkAarsak(request.getSoknadRequest().getSoktOm()));
        } else if (feedbackMatchFound(FEEDBACK_FORSDKUDDSBEREGNING_UGYLDIG_FORSORGERKODE, bisys.bisysPage().errors())) {
            forskuddsberegning.selectSivilstand(request.getSivilstandBm());
        }
    }

    private void lagreOgForskudd(BisysApplication bisys) throws BidragRequestProcessingException {
        try {
            Soknad soknad = (Soknad) BisysUiSupport.getActiveBisysPage(bisys);
            lagreOgForskudd(soknad);
        } catch (ElementNotFoundException | NoSuchElementException | ClassCastException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(), e);
        }

    }

    private void lagreOgForskudd(Soknad soknad) throws BidragRequestProcessingException {
        try {
            soknad.lagreOgForskudd().click();
        } catch (NoSuchElementException | ElementNotFoundException e) {
            throw new BidragRequestProcessingException("LagreOgForskudd-button not visible. Check logged on enhet", soknad, e);
        }
    }

}
