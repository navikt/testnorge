package no.nav.dolly.consumer.aareg;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregResponse;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;

@RunWith(SpringRunner.class)
@RestClientTest(TestnorgeAaregConsumer.class)
@ActiveProfiles("test")
public class TestnorgeAaregConsumerTest {

    @Autowired
    private TestnorgeAaregConsumer testnorgeAaregConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${providers.aaregdata.url}")
    private String serverUrl;

    private String ident = "01010101010";
    private String miljoe = "t0";
    private RsAaregOpprettRequest opprettRequest;
    private RsAaregOppdaterRequest oppdaterRequest;
    private RsAaregResponse opprettResponse;
    private RsAaregResponse oppdaterResponse;
    private Map<String, String> slettResponse;

    @Before
    public void setUp() {
        opprettRequest = RsAaregOpprettRequest.builder()
                .arbeidsforhold(RsArbeidsforhold.builder()
                        .build())
                .environments(Collections.singletonList(miljoe))
                .build();
        Map<String, String> status = new HashMap<>();
        status.put(miljoe, "OK");
        opprettResponse = RsAaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        oppdaterRequest = new RsAaregOppdaterRequest(LocalDateTime.of(2019, 1, 1, 0, 0, 0));
        oppdaterRequest.setArbeidsforhold(RsArbeidsforhold.builder().build());
        oppdaterRequest.setEnvironments(Collections.singletonList(miljoe));

        oppdaterResponse = RsAaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        slettResponse = new HashMap<>();
        slettResponse.put(miljoe, "OK");
    }

    @Test
    public void opprettArbeidsforhold() throws JsonProcessingException {
        String expectedUri = serverUrl + "/v1/arbeidsforhold";
        stubOpprettArbeidsforhold(expectedUri, opprettResponse);

        RsAaregResponse response = testnorgeAaregConsumer.opprettArbeidsforhold(opprettRequest);

        assertThat(response.getStatusPerMiljoe().get(miljoe), equalTo("OK"));
    }

    @Test
    public void oppdaterArbeidsforhold() throws JsonProcessingException {
        String expectedUri = serverUrl + "/v1/arbeidsforhold";
        stubOppdaterArbeidsforhold(expectedUri, oppdaterResponse);

        RsAaregResponse response = testnorgeAaregConsumer.oppdaterArbeidsforhold(oppdaterRequest);

        assertThat(response.getStatusPerMiljoe().get(miljoe), equalTo("OK"));
    }

    @Test
    public void hentArbeidsforhold() {
        String expectedUri = serverUrl + "/v1/arbeidsforhold?ident={ident}&miljoe={miljoe}";
        stubHentArbeidsforhold(expectedUri);

        testnorgeAaregConsumer.hentArbeidsforhold(ident, miljoe);
    }

    @Test
    public void slettArbeidsforhold() throws JsonProcessingException {
        String expectedUri = serverUrl + "/v1/arbeidsforhold?ident={ident}";
        stubSlettIdentFraAlleMiljoer(expectedUri, slettResponse);

        Map<String, String> response = testnorgeAaregConsumer.slettArbeidsforholdFraAlleMiljoer(ident);

        assertThat(response.get(miljoe), equalTo("OK"));
    }

    private void stubOpprettArbeidsforhold(String expectedUri, RsAaregResponse response) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(asJsonString(opprettRequest)))
                .andRespond(
                        withStatus(HttpStatus.CREATED)
                                .body(asJsonString(response))
                                .contentType(MediaType.APPLICATION_JSON));
    }

    private void stubOppdaterArbeidsforhold(String expectedUri, RsAaregResponse response) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().string(
                        String.format(
                                "{\"environments\":%s,\"arbeidsforhold\":%s,\"arkivreferanse\":%s,\"rapporteringsperiode\":%s}",
                                asJsonString(oppdaterRequest.getEnvironments()),
                                asJsonString(oppdaterRequest.getArbeidsforhold()),
                                asJsonString(oppdaterRequest.getArkivreferanse()),
                                asJsonString(oppdaterRequest.getRapporteringsperiode().format(DateTimeFormatter.ISO_DATE_TIME)))
                ))
                .andRespond(withSuccess(asJsonString(response), MediaType.APPLICATION_JSON));
    }

    private void stubHentArbeidsforhold(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, ident, miljoe))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));
    }

    private void stubSlettIdentFraAlleMiljoer(String expectedUri, Map<String, String> response) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri, ident))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess(asJsonString(response), MediaType.APPLICATION_JSON));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}