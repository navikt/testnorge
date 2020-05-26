package no.nav.registre.testnorge.elsam.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.testnorge.elsam.consumer.rs.response.aareg.AaregResponse;

@RunWith(SpringRunner.class)
@RestClientTest(AaregstubConsumer.class)
@ActiveProfiles("test")
public class AaregstubConsumerTest {

    @Autowired
    private AaregstubConsumer aaregstubConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge.rest-api.aaregstub}")
    private String serverUrl;

    private String fnr1 = "30073245199";
    private List<String> expectedIdenterIAaregstub;

    @Before
    public void setUp() {
        expectedIdenterIAaregstub = new ArrayList<>(Collections.singletonList(fnr1));
    }

    @Test
    public void shouldHenteAlleIdenterIAaregstub() {
        String expectedUri = serverUrl + "/v1/hentAlleArbeidstakere";
        stubAaregstubHentAlleIdenter(expectedUri);

        List<String> response = aaregstubConsumer.hentAlleIdenterIStub();

        assertThat(response, containsInAnyOrder(expectedIdenterIAaregstub.get(0)));
    }

    @Test
    public void shouldHenteArbeidsforholdTilIdent() throws IOException {
        String expectedUri = serverUrl + "/v1/hentIdentMedArbeidsforhold/" + fnr1;
        stubAaregstubHentArbeidsforhold(expectedUri);

        AaregResponse response = aaregstubConsumer.hentArbeidsforholdTilIdent(fnr1);

        assertThat(response.getFnr(), equalTo(fnr1));
        assertThat(response.getArbeidsforhold().get(0).getAnsettelsesPeriode().getFom(), equalTo("2008-10-01T00:00:00"));
        assertThat(response.getArbeidsforhold().get(0).getArbeidsavtale().getEndringsdatoStillingsprosent(), equalTo("2008-10-01T00:00:00"));
        assertThat(response.getArbeidsforhold().get(0).getArbeidsgiver().getOrgnummer(), equalTo("975293378"));
    }

    private void stubAaregstubHentAlleIdenter(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[\"" + expectedIdenterIAaregstub.get(0) + "\"]", MediaType.APPLICATION_JSON));
    }

    private void stubAaregstubHentArbeidsforhold(String expectedUri) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL resource = Resources.getResource("testdata/arbeidsforhold_fnr1.json");
        AaregResponse returnValue = objectMapper.readValue(resource, new TypeReference<AaregResponse>() {
        });

        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(returnValue), MediaType.APPLICATION_JSON));
    }
}