package no.nav.registre.bisys.consumer.ui.vedtak.types;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknFraConstants;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.FatteVedtak;
import no.nav.bidrag.ui.bisys.soknad.fattevedtak.Vedtakslinje;
import no.nav.registre.bisys.consumer.rs.request.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;

@Component
public class OpphorConsumer {

    @Autowired
    private BidragConsumer bidragConsumer;

    public void runOpphor(BisysApplication bisys, SynthesizedBidragRequest request) throws BidragRequestProcessingException {
        Soknad soknad = (Soknad) BisysUiSupport.getActiveBisysPage(bisys);

        if (KodeSoknFraConstants.BP.equals(request.getSoknadRequest().getSoknadFra())
                && KodeSoknGrKomConstants.BIDRAG_18_AAR_INNKREVING.equals(request.getSoknadRequest().getSoktOm())) {
            bidragConsumer.runBidrag(bisys, request);
        } else {

            lagreOgFatteVedtak(soknad);

            FatteVedtak fatteVedtak = (FatteVedtak) BisysUiSupport.getActiveBisysPage(bisys);
            List<Vedtakslinje> vedtakslinjer = fatteVedtak.vedtakslinjer();

            for (Vedtakslinje vedtakslinje : vedtakslinjer) {
                selectResultatDropdown(request, soknad, vedtakslinje);
            }

            fatteVedtak.executeFatteVedtak().click();
        }
    }

    private void selectResultatDropdown(SynthesizedBidragRequest request, Soknad soknad, Vedtakslinje vedtakslinje) throws BidragRequestProcessingException {
        try {
            vedtakslinje.resultatDropdown().select(request.getBeslaarsakKode());
        } catch (NoSuchElementException | ElementNotFoundException e) {
            throw new BidragRequestProcessingException("Result-dropdown not visible. Check logged on enhet", soknad, e);
        }
    }

    private void lagreOgFatteVedtak(Soknad soknad) throws BidragRequestProcessingException {
        try {
            soknad.lagreOgFatteVedtak().click();
        } catch (NoSuchElementException | ElementNotFoundException e) {
            throw new BidragRequestProcessingException("LagreOgFatteVedtak-button not visible. Check logged on enhet", soknad, e);
        }
    }
}
