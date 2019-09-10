package no.nav.registre.bisys.consumer.ui.vedtak.executors;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.ActiveBisysPage;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Forskuddsberegning;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.FatteVedtak;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.ui.vedtak.BisysUiYtelseberegningConsumer;

@Component
public class ForskuddExecutor {

    @Autowired
    private BisysUiYtelseberegningConsumer ytelsebereging;

    public void runForskudd(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {

        ActiveBisysPage activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

        try {
            Soknad soknad = (Soknad) bisys.getActivePage(activePage);
            lagreOgForskudd(soknad);

            ytelsebereging.fulfillInntekterAndBoforhold(bisys, request);

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            Forskuddsberegning forskuddsberegning = (Forskuddsberegning) bisys.getActivePage(activePage);
            ytelsebereging.fulfillForskuddsberegning(forskuddsberegning, request);
            forskuddsberegning.lagreOgBeregn().click();
            forskuddsberegning.lagreBeregnFatteVedtak().click();

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            FatteVedtak fatteVedtak = (FatteVedtak) bisys.getActivePage(activePage);
            fatteVedtak.executeFatteVedtak().click();

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
