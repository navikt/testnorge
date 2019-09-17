package no.nav.registre.tss.consumer.rs;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.tss.domain.Person;

@RunWith(SpringRunner.class)
@RestClientTest(TssSyntetisererenConsumer.class)
@ActiveProfiles("test")
public class TssSyntetisererenConsumerTest {

    @Autowired
    private TssSyntetisererenConsumer tssSyntetisererenConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${synthdata-tss-api-url}")
    private String serverUrl;

    @Test
    public void shouldHenteSyntetiskeTssRutiner() {
        List<Person> identer = new ArrayList<>();
        server.expect(requestToUriTemplate(serverUrl + "/v1/generate_tss_file")).andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        List<String> syntetiskeTssRutiner = tssSyntetisererenConsumer.hentSyntetiskeTssRutiner(identer);

        // TODO: Fullfør test ved å sjekke resultat når syntpakken er blitt oppdatert
    }
}