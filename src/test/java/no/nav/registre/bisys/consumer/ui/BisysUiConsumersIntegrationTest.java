package no.nav.registre.bisys.consumer.ui;

import static no.nav.registre.bisys.testutils.ResourceUtils.getResourceFileContent;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.ApplicationStarter;
import no.nav.registre.bisys.LocalApplicationStarter;
import no.nav.registre.bisys.consumer.rs.request.BisysRequestAugments;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiFatteVedtakConsumer;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiSoknadConsumer;

@ActiveProfiles("local-integration-test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LocalApplicationStarter.class)
@AutoConfigureWireMock(port = 0)
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = ApplicationStarter.class) })
public class BisysUiConsumersIntegrationTest {

    @Autowired
    private BisysUiSupport navigationSupport;

    @Autowired
    private BisysUiSoknadConsumer soknadConsumer;

    @Autowired
    private BisysUiFatteVedtakConsumer fatteVedtakConsumer;

    @Autowired
    private BisysRequestAugments bisysRequestAugments;

    private TestnorgeToBisysMapper testnorgeToBisysMapper = Mappers.getMapper(TestnorgeToBisysMapper.class);

    @Test
    public void openOrCreateSoknadIntegrationTest() throws BidragRequestProcessingException,
            JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        BisysApplication bisys = null;

        String meldinger = getResourceFileContent("bidragsmeldingEttBarn.json");

        List<SyntetisertBidragsmelding> bidragsmeldinger = mapper.readValue(meldinger, new TypeReference<List<SyntetisertBidragsmelding>>() {
        });

        if (bidragsmeldinger != null && bidragsmeldinger.size() > 0) {
            bisys = navigationSupport.logon();
        }

        for (SyntetisertBidragsmelding bidragsmelding : bidragsmeldinger) {
            soknadConsumer.openOrCreateSoknad(bisys,
                    testnorgeToBisysMapper.testnorgeToBisys(bidragsmelding, bisysRequestAugments));
        }
    }

    @Test
    public void createFatteVedtakIntegrationTest() throws JsonParseException, JsonMappingException,
            IOException, BidragRequestProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        BisysApplication bisys = null;

        String meldinger = getResourceFileContent("bidragsmeldingForQ8.json");

        List<SyntetisertBidragsmelding> bidragsmeldinger = mapper.readValue(meldinger, new TypeReference<List<SyntetisertBidragsmelding>>() {
        });

        if (bidragsmeldinger != null && bidragsmeldinger.size() > 0) {
            bisys = navigationSupport.logon();
        }

        for (SyntetisertBidragsmelding bidragsmelding : bidragsmeldinger) {
            SynthesizedBidragRequest request = testnorgeToBisysMapper.testnorgeToBisys(bidragsmelding, bisysRequestAugments);
            soknadConsumer.openOrCreateSoknad(bisys, request);
            fatteVedtakConsumer.runFatteVedtak(bisys, request);
        }

    }
}
