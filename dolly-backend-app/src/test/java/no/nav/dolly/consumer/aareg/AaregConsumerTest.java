package no.nav.dolly.consumer.aareg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.aareg.AaregConsumer;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.AaregResponse;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.properties.ProvidersProps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(AaregConsumer.class)
@ActiveProfiles("test")
public class AaregConsumerTest {

    @Autowired
    private AaregConsumer aaregConsumer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockRestServiceServer server;

    @MockBean
    private ProvidersProps providersProps;

    @Value("${providers.aaregdata.url}")
    private String serverUrl;

    private final String ident = "01010101010";
    private final String miljoe = "t0";
    private AaregOpprettRequest opprettRequest;
    private AaregResponse opprettResponse;
    private AaregResponse slettResponse;

    @Before
    public void setUp() {
        when(providersProps.getAaregdata()).thenReturn(ProvidersProps.Aaregdata.builder().url(serverUrl).build());

        server = MockRestServiceServer.createServer(restTemplate);

        opprettRequest = AaregOpprettRequest.builder()
                .arbeidsforhold(Arbeidsforhold.builder()
                        .build())
                .environments(Collections.singletonList(miljoe))
                .build();
        Map<String, String> status = new HashMap<>();
        status.put(miljoe, "OK");
        opprettResponse = AaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        slettResponse = AaregResponse.builder()
                .statusPerMiljoe(status)
                .build();
    }

    @Test
    public void opprettArbeidsforhold() throws JsonProcessingException {
        String expectedUri = serverUrl + "/api/v1/arbeidsforhold";
        stubOpprettArbeidsforhold(expectedUri, opprettResponse);

        AaregResponse response = aaregConsumer.opprettArbeidsforhold(opprettRequest);

        assertThat(response.getStatusPerMiljoe().get(miljoe), equalTo("OK"));
    }

    @Test
    public void hentArbeidsforhold() {
        String expectedUri = serverUrl + "/api/v1/arbeidsforhold?ident={ident}&miljoe={miljoe}";
        stubHentArbeidsforhold(expectedUri);

        aaregConsumer.hentArbeidsforhold(ident, miljoe);
    }

    @Test
    public void slettArbeidsforhold() throws JsonProcessingException {
        String expectedUri = serverUrl + "/api/v1/arbeidsforhold?ident={ident}";
        stubSlettIdentFraAlleMiljoer(expectedUri, slettResponse);

        AaregResponse response = aaregConsumer.slettArbeidsforholdFraAlleMiljoer(ident);

        assertThat(response.getStatusPerMiljoe().get(miljoe), equalTo("OK"));
    }

    private void stubOpprettArbeidsforhold(String expectedUri, AaregResponse response) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(asJsonString(opprettRequest)))
                .andRespond(
                        withStatus(HttpStatus.CREATED)
                                .body(asJsonString(response))
                                .contentType(MediaType.APPLICATION_JSON));
    }

    private void stubHentArbeidsforhold(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, ident, miljoe))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));
    }

    private void stubSlettIdentFraAlleMiljoer(String expectedUri, AaregResponse response) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri, ident))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess(asJsonString(response), MediaType.APPLICATION_JSON));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}