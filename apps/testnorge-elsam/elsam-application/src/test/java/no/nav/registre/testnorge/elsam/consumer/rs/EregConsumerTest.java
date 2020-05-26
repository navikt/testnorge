package no.nav.registre.testnorge.elsam.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
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

import no.nav.registre.testnorge.elsam.consumer.rs.response.ereg.EregResponse;

@RunWith(SpringRunner.class)
@RestClientTest(EregConsumer.class)
@ActiveProfiles("test")
public class EregConsumerTest {

    @Autowired
    private EregConsumer eregConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${ereg.api.url}")
    private String serverUrl;

    private String orgnummer = "975293378";

    @Test
    public void shouldHenteArbeidsgiversNavn() throws IOException {
        String expectedUri = serverUrl + "/v1/organisasjon/{orgnummer}?inkluderHierarki=false&inkluderHistorikk=false";
        stubEregHentArbeidsgiversNavn(expectedUri);

        EregResponse response = eregConsumer.hentEregdataFraOrgnummer(orgnummer);

        assertThat(response.getOrganisasjonsnummer(), equalTo(orgnummer));
        assertThat(response.getNavn().getNavnelinje1(), equalTo("ASKVOLL KOMMUNE SKULEADMINISTRASJON"));
    }

    private void stubEregHentArbeidsgiversNavn(String expectedUri) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL resource = Resources.getResource("testdata/ereg_data_orgnummer1.json");
        EregResponse returnValue = objectMapper.readValue(resource, new TypeReference<EregResponse>() {
        });

        server.expect(requestToUriTemplate(expectedUri, orgnummer))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(returnValue), MediaType.APPLICATION_JSON));
    }
}