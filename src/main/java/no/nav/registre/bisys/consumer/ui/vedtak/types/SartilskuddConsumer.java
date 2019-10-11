package no.nav.registre.bisys.consumer.ui.vedtak.types;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Sartilskudd;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.FatteVedtak;
import no.nav.registre.bisys.consumer.rs.request.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.vedtak.BisysUiYtelseberegningConsumer;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;

@Component
public class SartilskuddConsumer {

    @Autowired
    private BisysUiYtelseberegningConsumer ytelsebereging;

    public void runSartilskudd(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {

        try {
            Soknad soknad = (Soknad) BisysUiSupport.getActiveBisysPage(bisys);
            lagreOgSartilskudd(soknad);

            Sartilskudd sartilskudd = (Sartilskudd) BisysUiSupport.getActiveBisysPage(bisys);
            sartilskudd.kravbelop().setValue(Integer.toString(request.getKravbelop()));
            sartilskudd.godkjentBelop().setValue(Integer.toString(request.getGodkjentBelop()));
            sartilskudd.lagreOgInntekt().click();

            ytelsebereging.fulfillInntekterAndBoforhold(bisys, request);

            sartilskudd.lagreOgBeregn().click();
            sartilskudd.lagreOgFatteVedtak().click();

            FatteVedtak fatteVedtak = (FatteVedtak) BisysUiSupport.getActiveBisysPage(bisys);
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
