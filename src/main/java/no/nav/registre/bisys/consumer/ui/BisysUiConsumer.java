package no.nav.registre.bisys.consumer.ui;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.rs.request.BisysRequestAugments;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiFatteVedtakConsumer;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiSoknadConsumer;

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
    private BisysRequestAugments bisysRequestAugments;

    private TestnorgeToBisysMapper testnorgeToBisysMapper = Mappers.getMapper(TestnorgeToBisysMapper.class);

    public void createVedtak(SyntetisertBidragsmelding bidragsmelding)
            throws BidragRequestProcessingException {

        BisysApplication bisys = null;

        if (bidragsmelding != null) {
            bisys = navigationSupport.logon();
        }

        SynthesizedBidragRequest request = testnorgeToBisysMapper.testnorgeToBisys(bidragsmelding, bisysRequestAugments);
        log.info("Processing SyntetisertBidragsmelding with barnetsFnr {}",
                bidragsmelding.getBarnetsFnr());

        soknadConsumer.openOrCreateSoknad(bisys, request);
        fatteVedtakConsumer.runFatteVedtak(bisys, request);

    }
}
