package no.nav.registre.bisys.consumer.ui.vedtak;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.ActiveBisysPage;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.vedtak.executors.BidragExecutor;
import no.nav.registre.bisys.consumer.ui.vedtak.executors.ForskuddExecutor;
import no.nav.registre.bisys.consumer.ui.vedtak.executors.SartilskuddExecutor;

@Component
public class BisysUiFatteVedtakConsumer {

    public static final String KODE_BESL_AARSAK_FRITATT_IKKE_SOKT = "GIFR";
    public static final String KODE_BESL_AARSAK_ILAGT_IKKE_SOKT = "GIGI";

    @Autowired
    private BidragExecutor bidragExecutor;

    @Autowired
    private ForskuddExecutor forskuddExecutor;

    @Autowired
    private SartilskuddExecutor sartilskuddExecutor;

    public static final Map<String, String> beslAarsakDekodeMap() {

        Map<String, String> dekodeMap = new HashMap<>();
        dekodeMap.put(KODE_BESL_AARSAK_FRITATT_IKKE_SOKT, "Fritatt, ikke søkt");
        dekodeMap.put(KODE_BESL_AARSAK_ILAGT_IKKE_SOKT, "Ilagt, ikke søkt");

        return dekodeMap;
    }

    /**
     * Runs the Bidragsberegning and FatteVedtak process
     * 
     * <code>
     *  - Bisys entry point: Soknad
     *  - Bisys exit point: Fatte vedtak
     * </code>
     * 
     * @param bisys
     *            BisysApplication
     * @param request
     *            SynthesizedBidragRequest containing all data required to complete the FatteVedtak process for a søknad
     * @throws BidragRequestProcessingException
     */
    public void runFatteVedtak(BisysApplication bisys, SynthesizedBidragRequest request)
            throws BidragRequestProcessingException {

        BisysUiSupport.checkCorrectActivePage(bisys, ActiveBisysPage.SOKNAD);

        if (KodeSoknGrKomConstants.FORSKUDD.equals(request.getSoktOm())) {
            forskuddExecutor.runForskudd(bisys, request);
        } else if (KodeSoknGrKomConstants.SARTILSKUDD_INNKREVING.equals(request.getSoktOm())) {
            sartilskuddExecutor.runSartilskuddInnkreving(bisys, request);
        } else {
            bidragExecutor.runBidragInnkreving(bisys, request);
        }
    }

}
