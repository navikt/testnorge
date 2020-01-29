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
import java.util.Collections;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggRequest;

@RunWith(SpringRunner.class)
@AutoConfigureWireMock(port = 0)
@RestClientTest(RettighetArenaForvalterConsumer.class)
@ActiveProfiles("test")
public class RettighetTilleggBrukereArenaForvalterConsumerTest {

    @Autowired
    private RettighetArenaForvalterConsumer consumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${arena-forvalteren.rest-api.url}")
    private String serverUrl;

    private List<RettighetRequest> rettigheter;

    @Before
    public void setUp() {
        rettigheter = new ArrayList<>(Collections.singletonList(
                new RettighetTilleggRequest()
        ));
    }

    @Test
    public void shouldOppretteRettighetTillegg() {
        stubArenaForvalterOpprettRettighetTilleggLaeremidler(serverUrl + "/v1/tilleggsstonad");

        var response = consumer.opprettRettighet(rettigheter);

        server.verify();

        assertThat(response.get(0).getNyeRettigheterTillegg().size(), equalTo(1));
    }

    private void stubArenaForvalterOpprettRettighetTilleggLaeremidler(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/tillegg/tillegg_forvalter_response.json")));
    }
}