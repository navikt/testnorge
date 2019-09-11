package no.nav.registre.bisys.consumer.ui;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.bisys.soknad.request.SoknadRequest;
import no.nav.registre.bisys.consumer.rs.request.BidragsmeldingAugments;
import no.nav.registre.bisys.consumer.rs.request.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.consumer.ui.sak.BisysUiSoknadConsumer;
import no.nav.registre.bisys.consumer.ui.vedtak.BisysUiFatteVedtakConsumer;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.exception.BidragRequestRuntimeException;

@Slf4j
@Component
public class BisysUiConsumer {

    public static final String INCORRECT_ENTRY_PAGE = "Incorrect entry page";

    public static final String PROCESSING_FAILED = "Processing failed.";

    @Autowired
    private BisysUiSupport navigationSupport;

    @Autowired
    private BisysUiSoknadConsumer soknadConsumer;

    @Autowired
    private BisysUiFatteVedtakConsumer fatteVedtakConsumer;

    @Autowired
    private BidragsmeldingAugments bisysRequestAugments;

    private TestnorgeToBisysMapper testnorgeToBisysMapper = Mappers.getMapper(TestnorgeToBisysMapper.class);

    public void createVedtak(SyntetisertBidragsmelding bidragsmelding)
            throws BidragRequestProcessingException {

        BisysApplication bisys = null;

        if (bidragsmelding != null) {
            bisys = navigationSupport.logon();

            log.info("Processing SyntetisertBidragsmelding with barnetsFnr {}",
                    bidragsmelding.getBarnetsFnr());
        }

        SynthesizedBidragRequest request = testnorgeToBisysMapper.bidragsmeldingToBidragRequest(bisysRequestAugments);
        SoknadRequest soknadRequest = testnorgeToBisysMapper.bidragsmeldingToSoknadRequest(bidragsmelding);
        request.setSoknadRequest(soknadRequest);

        if (bisys != null) {
            soknadConsumer.openOrCreateSoknad(bisys, request.getSoknadRequest());
            fatteVedtakConsumer.runFatteVedtak(bisys, request);
        } else {
            throw new BidragRequestRuntimeException("Bisys logon failed!");
        }

    }
}
