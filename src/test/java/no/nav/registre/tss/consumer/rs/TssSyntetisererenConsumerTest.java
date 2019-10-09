package no.nav.registre.tss.consumer.rs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import wiremock.com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.registre.tss.consumer.rs.response.TssMessage;
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

    private String fnr1 = "23026325811";
    private String navn1 = "GLOBUS IHERDIG STRAFFET";
    private String fnr2 = "08016325431";
    private String navn2 = "BORD OPPSTEMT ELEKTRONISK";

    @Test
    public void shouldHenteSyntetiskeTssRutiner() throws IOException {
        Person person1 = new Person(fnr1, navn1);
        Person person2 = new Person(fnr2, navn2);
        List<Person> identer = new ArrayList<>();
        identer.add(person1);
        identer.add(person2);

        server.expect(requestToUriTemplate(serverUrl + "/v1/generate_tss_messages/json")).andRespond(withSuccess(getJsonResponse(), MediaType.APPLICATION_JSON));
        Map<String, List<TssMessage>> identerMedRutiner = tssSyntetisererenConsumer.hentSyntetiskeTssRutiner(identer);

        assertThat(identerMedRutiner.get(fnr1).get(0).getNavn(), equalTo(navn1));
        assertThat(identerMedRutiner.get(fnr1).get(1).getIdAlternativ(), equalTo(fnr1));
        assertThat(identerMedRutiner.get(fnr2).get(0).getNavn(), equalTo(navn2));
        assertThat(identerMedRutiner.get(fnr2).get(1).getIdAlternativ(), equalTo(fnr2));
    }

    private String getJsonResponse() throws IOException {
        URL resource = Resources.getResource("syntetiske_rutiner.json");
        return new ObjectMapper().readValue(resource, JsonNode.class).toString();
    }
}