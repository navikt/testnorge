package no.nav.registre.bisys.consumer.ui.vedtak;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.BisysApplication.BisysPageTitle;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknTypeConstants;
import no.nav.registre.bisys.consumer.rs.request.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.vedtak.types.BidragConsumer;
import no.nav.registre.bisys.consumer.ui.vedtak.types.ForskuddConsumer;
import no.nav.registre.bisys.consumer.ui.vedtak.types.OpphorConsumer;
import no.nav.registre.bisys.consumer.ui.vedtak.types.SartilskuddConsumer;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;

@Component
public class BisysUiFatteVedtakConsumer {

    public static final String KODE_BESL_AARSAK_FRITATT_IKKE_SOKT = "GIFR";
    public static final String KODE_BESL_AARSAK_ILAGT_IKKE_SOKT = "GIGI";

    @Autowired
    private BidragConsumer bidragExecutor;

    @Autowired
    private ForskuddConsumer forskuddExecutor;

    @Autowired
    private OpphorConsumer opphorExecutor;

    @Autowired
    private SartilskuddConsumer sartilskuddExecutor;

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

        BisysUiSupport.checkCorrectActivePage(bisys, BisysPageTitle.SOKNAD);

        String soktOm = request.getSoknadRequest().getSoktOm();
        String soknadstype = request.getSoknadRequest().getSoknadstype();

        if (KodeSoknGrKomConstants.FORSKUDD.equals(soktOm)) {
            forskuddExecutor.runForskudd(bisys, request);
        } else if (KodeSoknGrKomConstants.SARTILSKUDD_INNKREVING.equals(soktOm)) {
            sartilskuddExecutor.runSartilskuddInnkreving(bisys, request);
        } else if (KodeSoknGrKomConstants.BIDRAG_INNKREVING.equals(soktOm)) {
            if (KodeSoknTypeConstants.OPPHOR.equals(soknadstype)) {
                opphorExecutor.runOpphor(bisys, request);
            } else {
                bidragExecutor.runBidragInnkreving(bisys, request);
            }
        } else {
            throw new BidragRequestProcessingException(BisysUiSupport.getActiveBisysPage(bisys), new Exception(String.format("SoktOm %s not supported", KodeSoknGrKomConstants.dekodeMap().get(soktOm))));
        }
    }

}
