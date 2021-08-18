package no.nav.registre.aareg.consumer.rs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.domain.RsArbeidsforhold;
import no.nav.registre.aareg.domain.RsPersonAareg;
import no.nav.registre.aareg.exception.ResponseNullPointerException;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(AaregstubConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AaregstubConsumerTest {

    @Autowired
    private AaregstubConsumer aaregstubConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${aaregstub.rest.api.url}")
    private String serverUrl;

    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";
    private final String fnr3 = "02020202020";

    @Test
    public void shouldGetAlleArbeidstakere() {
        var expectedUri = serverUrl + "/v1/hentAlleArbeidstakere";
        stubAaregstubHentArbeidstakereConsumer(expectedUri);

        var response = aaregstubConsumer.hentEksisterendeIdenter();

        assertThat(response.get(0), equalTo(fnr1));
        assertThat(response.get(1), equalTo(fnr2));
        assertThat(response.get(2), equalTo(fnr3));
    }

    @Test
    public void shouldSendSyntetiskeMeldinger() throws JsonProcessingException {
        List<RsAaregOpprettRequest> syntetiserteMeldinger = new ArrayList<>(Arrays.asList(
                RsAaregOpprettRequest.builder()
                        .arbeidsforhold(RsArbeidsforhold.builder()
                                .arbeidstaker(RsPersonAareg.builder()
                                        .ident(fnr1)
                                        .build())
                                .build())
                        .build(),
                RsAaregOpprettRequest.builder()
                        .arbeidsforhold(RsArbeidsforhold.builder()
                                .arbeidstaker(RsPersonAareg.builder()
                                        .ident(fnr2)
                                        .build())
                                .build())
                        .build()));

        var expectedUri = serverUrl + "/v1/lagreArbeidsforhold";
        stubAaregstubLagreConsumer(expectedUri, syntetiserteMeldinger);

        var statusFraAaregstubResponse = aaregstubConsumer.sendTilAaregstub(syntetiserteMeldinger);

        assertThat(statusFraAaregstubResponse.size(), equalTo(2));
        assertThat(statusFraAaregstubResponse.get(0), equalTo(fnr1));
        assertThat(statusFraAaregstubResponse.get(1), equalTo(fnr2));
    }

    @Test
    public void shouldThrowExceptionOnEmptyResponse() {
        var expectedUri = serverUrl + "/v1/hentAlleArbeidstakere";
        stubAaregstubWithEmptyBody(expectedUri);

        Assertions.assertThrows(ResponseNullPointerException.class, () -> aaregstubConsumer.hentEksisterendeIdenter());
    }

    @Test
    public void shouldDeleteIdenter() throws JsonProcessingException {
        List<Long> expectedIds = new ArrayList<>(Arrays.asList(123L, 234L));

        var expectedUri = serverUrl + "/v1/slettIdent/{ident}";
        stubAaregstubSlettIdenter(expectedUri, fnr1, expectedIds);

        var response = aaregstubConsumer.slettIdenterFraAaregstub(Collections.singletonList(fnr1));

        assertThat(response.getIdentermedArbeidsforholdIdSomBleSlettet().get(fnr1),
                IsIterableContainingInOrder.contains(expectedIds.get(0), expectedIds.get(1)));
    }

    private void stubAaregstubHentArbeidstakereConsumer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[\"" + fnr1 + "\", \"" + fnr2 + "\", \"" + fnr3 + "\"]", MediaType.APPLICATION_JSON));
    }

    private void stubAaregstubLagreConsumer(
            String expectedUri,
            List<RsAaregOpprettRequest> syntetiserteMeldinger
    ) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(asJsonString(syntetiserteMeldinger)))
                .andRespond(withSuccess("[\"" + fnr1 + "\", \"" + fnr2 + "\"]", MediaType.APPLICATION_JSON));
    }

    private void stubAaregstubWithEmptyBody(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());
    }

    private void stubAaregstubSlettIdenter(
            String expectedUri,
            String ident,
            List<Long> expectedResponse
    ) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri, ident))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess(asJsonString(expectedResponse), MediaType.APPLICATION_JSON));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
