package no.nav.registre.bisys.consumer.ui.vedtak.executors;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.ActiveBisysPage;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Bidragsberegning;
import no.nav.bidrag.ui.bisys.soknad.bidragsberegning.Underholdskostnad;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.FatteVedtak;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.ui.vedtak.BisysUiYtelseberegningConsumer;

@Component
public class BidragExecutor {

    @Autowired
    private BisysUiYtelseberegningConsumer ytelsebereging;

    /**
     * 
     * 
     * @param bisys
     * @param request
     * @throws BidragRequestProcessingException
     */
    public void runBidragInnkreving(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {
        try {
            ActiveBisysPage activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            Soknad soknad = (Soknad) bisys.getActivePage(activePage);
            soknad.lagreOgBidrag().click();

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            Underholdskostnad underholdskostnad = (Underholdskostnad) bisys.getActivePage(activePage);
            underholdskostnad.lagreTilInntekter().click();

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            ytelsebereging.fulfillInntekterAndBoforhold(bisys, request);

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            Bidragsberegning bidragsberegning = (Bidragsberegning) bisys.getActivePage(activePage);
            ytelsebereging.fulfillBidragsberegning(bidragsberegning, request);
            bidragsberegning.lagreOgBeregne().click();
            bidragsberegning.lagreOgFatteVedtak().click();

            activePage = ActiveBisysPage.getActivePage(bisys.getBisysPageTitle()).get();

            FatteVedtak fatteVedtak = (FatteVedtak) bisys.getActivePage(activePage);
            ytelsebereging.completeFatteVedtak(fatteVedtak, request);
            fatteVedtak.executeFatteVedtak().click();

        } catch (ElementNotFoundException | NoSuchElementException | ClassCastException e) {
            throw new BidragRequestProcessingException(bisys.bisysPage(), e);
        }
    }

}
