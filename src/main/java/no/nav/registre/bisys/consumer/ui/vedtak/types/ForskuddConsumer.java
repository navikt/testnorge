package no.nav.registre.bisys.consumer.ui.vedtak.types;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import no.nav.bidrag.ui.bisys.BisysApplication;
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

    public void runForskudd(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {

        try {
            Soknad soknad = (Soknad) BisysUiSupport.getActiveBisysPage(bisys);
            lagreOgForskudd(soknad);

            ytelsebereging.fulfillInntekterAndBoforhold(bisys, request);

            Forskuddsberegning forskuddsberegning = (Forskuddsberegning) BisysUiSupport.getActiveBisysPage(bisys);
            ytelsebereging.fulfillForskuddsberegning(forskuddsberegning, request);
            forskuddsberegning.lagreOgBeregn().click();
            forskuddsberegning.lagreBeregnFatteVedtak().click();

            FatteVedtak fatteVedtak = (FatteVedtak) BisysUiSupport.getActiveBisysPage(bisys);
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
