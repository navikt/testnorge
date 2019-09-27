package no.nav.registre.bisys.consumer.ui.vedtak.types;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknFraConstants;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Bidragsberegning;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Underholdskostnad;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.FatteVedtak;
import no.nav.registre.bisys.consumer.rs.request.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.vedtak.BisysUiYtelseberegningConsumer;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;

@Component
public class BidragConsumer {

    @Autowired
    private BisysUiYtelseberegningConsumer ytelsebereging;

    /**
     * 
     * 
     * @param bisys
     * @param request
     * @throws BidragRequestProcessingException
     */
    public void runBidrag(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {
        try {

            Soknad soknad = (Soknad) BisysUiSupport.getActiveBisysPage(bisys);

            if (KodeSoknFraConstants.BARN_ELDRE_ENN_18.equals(request.getSoknadRequest().getSoknadFra())) {
                soknad.lagreOg18ArsBidrag().click();
            } else {
                soknad.lagreOgBidrag().click();
            }

            Underholdskostnad underholdskostnad = (Underholdskostnad) BisysUiSupport.getActiveBisysPage(bisys);
            underholdskostnad.lagreTilInntekter().click();

            ytelsebereging.fulfillInntekterAndBoforhold(bisys, request);

            Bidragsberegning bidragsberegning = (Bidragsberegning) BisysUiSupport.getActiveBisysPage(bisys);
            ytelsebereging.fulfillBidragsberegning(bidragsberegning, request);
            bidragsberegning.lagreOgBeregne().click();
            bidragsberegning.lagreOgFatteVedtak().click();

            FatteVedtak fatteVedtak = (FatteVedtak) BisysUiSupport.getActiveBisysPage(bisys);
            ytelsebereging.completeFatteVedtak(fatteVedtak, request);
            fatteVedtak.executeFatteVedtak().click();

        } catch (ElementNotFoundException | NoSuchElementException | ClassCastException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(), e);
        }
    }

}
