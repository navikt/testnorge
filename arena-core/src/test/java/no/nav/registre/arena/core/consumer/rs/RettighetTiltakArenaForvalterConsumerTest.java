package no.nav.registre.arena.core.consumer.rs;

import static no.nav.registre.arena.core.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltaksdeltakelseRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltakspengerRequest;

@RunWith(SpringRunner.class)
@AutoConfigureWireMock(port = 0)
@RestClientTest(RettighetTiltakArenaForvalterConsumer.class)
@ActiveProfiles("test")
public class RettighetTiltakArenaForvalterConsumerTest {

    @Autowired
    private RettighetTiltakArenaForvalterConsumer consumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${arena-forvalteren.rest-api.url}")
    private String serverUrl;

    private List<RettighetRequest> rettigheter;

    @Before
    public void setUp() {
        rettigheter = new ArrayList<>(Arrays.asList(
                new RettighetTiltaksdeltakelseRequest(),
                new RettighetTiltakspengerRequest()
        ));
    }

    @Test
    public void shouldOppretteRettighetTiltak() {
        stubArenaForvalterOpprettRettighetTiltaksdeltakelse(serverUrl + "/v1/tiltaksdeltakelse");
        stubArenaForvalterOpprettRettighetTiltakspenger(serverUrl + "/v1/tiltakspenger");

        var response = consumer.opprettRettighet(rettigheter);

        server.verify();

        assertThat(response.get(0).getFeiledeRettigheter().size(), equalTo(0));
        assertThat(response.get(1).getNyeRettigheterTiltak().size(), equalTo(1));
    }

    private void stubArenaForvalterOpprettRettighetTiltaksdeltakelse(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/tiltak/tiltaksdeltakelse_forvalter_response.json")));
    }

    private void stubArenaForvalterOpprettRettighetTiltakspenger(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/tiltak/tiltakspenger_forvalter_response.json")));
    }
}