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

import no.nav.registre.arena.core.consumer.rs.request.RettighetAap115Request;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;

@RunWith(SpringRunner.class)
@AutoConfigureWireMock(port = 0)
@RestClientTest(RettighetAapArenaForvalterConsumer.class)
@ActiveProfiles("test")
public class RettighetAapArenaForvalterConsumerTest {

    @Autowired
    private RettighetAapArenaForvalterConsumer consumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${arena-forvalteren.rest-api.url}")
    private String serverUrl;

    private List<RettighetRequest> rettigheter;

    @Before
    public void setUp() {
        rettigheter = new ArrayList<>(Arrays.asList(
                new RettighetAapRequest(),
                new RettighetAap115Request(),
                new RettighetUngUfoerRequest(),
                new RettighetTvungenForvaltningRequest(),
                new RettighetFritakMeldekortRequest()
        ));
    }

    @Test
    public void shouldOppretteRettighetAap() {
        stubArenaForvalterOpprettRettighetAapArena(serverUrl + "/v1/aap");
        stubArenaForvalterOpprettRettighetAap115Arena(serverUrl + "/v1/aap115");
        stubArenaForvalterOpprettRettighetUngUfoerArena(serverUrl + "/v1/aapungufor");
        stubArenaForvalterOpprettRettighetTvungenForvaltningArena(serverUrl + "/v1/aaptvungenforvaltning");
        stubArenaForvalterOpprettRettighetFritakMeldekortArena(serverUrl + "/v1/aapfritakmeldekort");

        var response = consumer.opprettRettighet(rettigheter);

        server.verify();

        assertThat(response.get(0).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(1).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(2).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(3).getNyeRettigheterAap().size(), equalTo(1));
        assertThat(response.get(4).getFeiledeRettigheter().size(), equalTo(1));
    }

    private void stubArenaForvalterOpprettRettighetAapArena(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/aap/aap_forvalter_response.json")));
    }

    private void stubArenaForvalterOpprettRettighetAap115Arena(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/aap/aap115_forvalter_response.json")));
    }

    private void stubArenaForvalterOpprettRettighetUngUfoerArena(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/aap/ung_ufoer_forvalter_response.json")));
    }

    private void stubArenaForvalterOpprettRettighetTvungenForvaltningArena(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/aap/tvungen_forvaltning_forvalter_response.json")));
    }

    private void stubArenaForvalterOpprettRettighetFritakMeldekortArena(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getResourceFileContent("files/aap/fritak_meldekort_forvalter_response.json")));
    }
}