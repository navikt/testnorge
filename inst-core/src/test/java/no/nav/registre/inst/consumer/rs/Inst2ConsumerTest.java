package no.nav.registre.inst.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static no.nav.registre.inst.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import no.nav.registre.inst.Institusjonsforholdsmelding;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class Inst2ConsumerTest {

    @Autowired
    private Inst2Consumer inst2Consumer;

    private String id = "orkestratoren";
    private String fnr1 = "01010101010";
    private Map<String, Object> token;
    private String tssEksternId = "123";
    private String date = "2019-01-01";

    @Before
    public void setUp() throws IOException {
        token = new ObjectMapper().readValue(getResourceFileContent("token.json"), new TypeReference<Map<String, Object>>() {
        });
    }

    @Test
    public void shouldGetTokenForInst2() {
        stubTokenProvider();

        Map<String, Object> actualToken = inst2Consumer.hentTokenTilInst2();

        assertThat(actualToken.get("idToken").toString(), containsString(token.get("idToken").toString()));
    }

    @Test
    public void shouldGetInstitusjonsmeldingerFromInst2() {
        stubGetInstitusjonsopphold();

        List<Institusjonsforholdsmelding> result = inst2Consumer.hentInstitusjonsoppholdFraInst2(token, fnr1);

        assertThat(result.get(0).getTssEksternId(), is("440"));
        assertThat(result.get(1).getTssEksternId(), is("441"));
    }

    @Test
    public void shouldAddInstitusjonsoppholdTilInst2() throws JsonProcessingException {
        Institusjonsforholdsmelding institusjonsforholdsmelding = Institusjonsforholdsmelding.builder().build();

        stubAddInstitusjonsopphold(institusjonsforholdsmelding);

        ResponseEntity result = inst2Consumer.leggTilInstitusjonsoppholdIInst2(token, institusjonsforholdsmelding);

        assertThat(result.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void shouldCheckWhetherInstitusjonIsValidOnDate() {
        stubFindInstitusjon();

        HttpStatus responseStatus = inst2Consumer.finnesInstitusjonPaaDato(token, tssEksternId, date);

        assertThat(responseStatus, is(HttpStatus.OK));
    }

    private void stubTokenProvider() {
        stubFor(get(urlPathEqualTo("/freg-token-provider/token/user"))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("username", equalTo("dummy"))
                .withHeader("password", equalTo("dummy"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("token.json"))));
    }

    private void stubGetInstitusjonsopphold() {
        stubFor(get(urlPathEqualTo("/inst2/web/api/person/institusjonsopphold"))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("Authorization", equalTo(token.get("tokenType") + " " + token.get("idToken")))
                .withHeader("Nav-Call-Id", equalTo(id))
                .withHeader("Nav-Consumer-Id", equalTo(id))
                .withHeader("Nav-Personident", equalTo(fnr1))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("institusjonsmelding.json"))));
    }

    private void stubAddInstitusjonsopphold(Institusjonsforholdsmelding institusjonsforholdsmelding) throws JsonProcessingException {
        stubFor(post(urlEqualTo("/inst2/web/api/person/institusjonsopphold?validatePeriod=true"))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("Authorization", equalTo(token.get("tokenType") + " " + token.get("idToken")))
                .withHeader("Nav-Call-Id", equalTo(id))
                .withHeader("Nav-Consumer-Id", equalTo(id))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(institusjonsforholdsmelding)))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())));
    }

    private void stubFindInstitusjon() {
        stubFor(get(urlEqualTo("/inst2/web/api/institusjon/oppslag/tssEksternId/" + tssEksternId + "?date=" + date))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("Authorization", equalTo(token.get("tokenType") + " " + token.get("idToken")))
                .withHeader("Nav-Call-Id", equalTo(id))
                .withHeader("Nav-Consumer-Id", equalTo(id))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"institusjonsnavn\": \"Institusjon1\","
                                + "  \"institusjonsnummer\": \"0123\","
                                + "  \"organisasjonsnummer\": \"808\","
                                + "  \"avdelinger\": [{"
                                + "      \"avdelingsnummer\": \"01\","
                                + "      \"tssEksternid\": \"" + tssEksternId + "\""
                                + "    }]}")));
    }
}