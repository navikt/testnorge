package no.nav.registre.bisys.service;

import static no.nav.registre.bisys.testutils.ResourceUtils.getResourceFileContent;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
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

import no.nav.registre.bisys.ApplicationStarter;
import no.nav.registre.bisys.LocalApplicationStarter;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;

@ActiveProfiles("local-integration-test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LocalApplicationStarter.class)
@AutoConfigureWireMock(port = 0)
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = ApplicationStarter.class) })
public class SyntetiseringServiceIntegrationTest {

    @Autowired
    private SyntetiseringService syntetiseringsService;

    @Test
    public void processBidragsmeldingerIntegrationTest() throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper mapper = new ObjectMapper();

        String meldinger = getResourceFileContent("bidragsmeldingQ2.json");

        List<SyntetisertBidragsmelding> bidragsmeldinger = mapper.readValue(meldinger, new TypeReference<List<SyntetisertBidragsmelding>>() {
        });

        syntetiseringsService.processBidragsmeldinger(bidragsmeldinger);

    }
}
