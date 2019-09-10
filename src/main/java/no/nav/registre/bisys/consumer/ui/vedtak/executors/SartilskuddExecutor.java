package no.nav.registre.bisys.consumer.ui.vedtak.executors;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.ActiveBisysPage;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Sartilskudd;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.FatteVedtak;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.ui.vedtak.BisysUiYtelseberegningConsumer;

@Component
public class SartilskuddExecutor {

    @Autowired
    private BisysUiYtelseberegningConsumer ytelsebereging;

    public void runSartilskuddInnkreving(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {
        ActiveBisysPage activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

        try {
            Soknad soknad = (Soknad) bisys.getActivePage(activePage);
            lagreOgSartilskudd(soknad);

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            Sartilskudd sartilskudd = (Sartilskudd) bisys.getActivePage(activePage);
            sartilskudd.kravbelop().setValue(Integer.toString(request.getSartilskuddKravbelop()));
            sartilskudd.godkjentBelop().setValue(Integer.toString(request.getSartilskuddGodkjentBelop()));
            sartilskudd.belopFradrag().setValue(Integer.toString(request.getSartilskuddFradrag()));
            sartilskudd.lagreOgInntekt().click();

            ytelsebereging.fulfillInntekterAndBoforhold(bisys, request);

            sartilskudd.lagreOgBeregn().click();
            sartilskudd.lagreOgFatteVedtak().click();

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            FatteVedtak fatteVedtak = (FatteVedtak) bisys.getActivePage(activePage);
            fatteVedtak.executeFatteVedtak().click();

        } catch (NoSuchElementException | ElementNotFoundException | ClassCastException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(), e);
        }
    }

    private void lagreOgSartilskudd(Soknad soknad) throws BidragRequestProcessingException {
        try {
            soknad.lagreOgSartilskudd().click();
        } catch (NoSuchElementException | ElementNotFoundException e) {
            throw new BidragRequestProcessingException("LagreOgSartilskudd-button not visible. Check logged on enhet", soknad, e);
        }
    }

}
