package no.nav.registre.inst.consumer.rs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.registre.inst.Institusjonsopphold;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.registre.inst.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureWireMock(port = 0)
public class Inst2ConsumerTest {

    @Autowired
    private Inst2Consumer inst2Consumer;

    private String id = "test";
    private String fnr1 = "01010101010";
    private String token = "Bearer abc";
    private String tssEksternId = "123";
    private LocalDate date = LocalDate.of(2019, 1, 1);
    private String miljoe = "t1";

    @Before
    public void before() {
        WireMock.reset();
    }

    @Test
    public void shouldGetInstitusjonsmeldingerFromInst2() {
        stubGetInstitusjonsopphold();

        var result = inst2Consumer.hentInstitusjonsoppholdFraInst2(token, id, id, miljoe, fnr1);

        assertThat(result.get(0).getTssEksternId(), is("440"));
        assertThat(result.get(0).getStartdato(), Matchers.equalTo(LocalDate.of(2013, 7, 3)));
        assertThat(result.get(1).getTssEksternId(), is("441"));
        assertThat(result.get(1).getStartdato(), Matchers.equalTo(LocalDate.of(2012, 4, 4)));
    }

    @Test
    public void shouldGetEmptyListOnBadRequest() {
        stubGetInstitusjonsoppholdWithBadRequest();

        var result = inst2Consumer.hentInstitusjonsoppholdFraInst2(token, id, id, miljoe, fnr1);

        assertThat(result, is(empty()));
    }

    @Test
    public void shouldAddInstitusjonsoppholdTilInst2() throws JsonProcessingException {
        var institusjonsopphold = Institusjonsopphold.builder().build();

        stubAddInstitusjonsopphold();

        var oppholdResponse = inst2Consumer.leggTilInstitusjonsoppholdIInst2(token, id, id, miljoe, institusjonsopphold);

        assertThat(oppholdResponse.getStatus(), is(HttpStatus.CREATED));
    }

    @Test
    public void shouldUpdateInstitusjonsoppholdIInst2() throws JsonProcessingException {
        var institusjonsopphold = Institusjonsopphold.builder().build();
        var oppholdId = 123L;

        stubUpdateInstitusjonsopphold(oppholdId);

        var response = inst2Consumer.oppdaterInstitusjonsoppholdIInst2(token, id, id, miljoe, oppholdId, institusjonsopphold);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldCheckWhetherInstitusjonIsValidOnDate() {
        stubFindInstitusjon();

        var responseStatus = inst2Consumer.finnesInstitusjonPaaDato(token, id, id, miljoe, tssEksternId, date);

        assertThat(responseStatus, is(HttpStatus.OK));
    }

    private void stubGetInstitusjonsopphold() {
        stubFor(get(urlPathEqualTo("/inst2/web/api/person/institusjonsopphold"))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("Authorization", equalTo(token))
                .withHeader("Nav-Call-Id", equalTo(id))
                .withHeader("Nav-Consumer-Id", equalTo(id))
                .withHeader("Nav-Personident", equalTo(fnr1))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("institusjonsmelding.json"))));
    }

    private void stubGetInstitusjonsoppholdWithBadRequest() {
        stubFor(get(urlPathEqualTo("/inst2/web/api/person/institusjonsopphold"))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("Authorization", equalTo(token))
                .withHeader("Nav-Call-Id", equalTo(id))
                .withHeader("Nav-Consumer-Id", equalTo(id))
                .withHeader("Nav-Personident", equalTo(fnr1))
                .willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())));
    }

    private void stubAddInstitusjonsopphold() {
        stubFor(post(urlPathMatching("(.*)/v1/institusjonsopphold/person"))
                .withQueryParam("environments", equalTo(miljoe))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("Authorization", equalTo(token))
                .withHeader("Nav-Call-Id", equalTo(id))
                .withHeader("Nav-Consumer-Id", equalTo(id))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())));
    }

    private void stubUpdateInstitusjonsopphold(Long oppholdId) {
        stubFor(put(urlEqualTo("/inst2/web/api/person/institusjonsopphold/" + oppholdId))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("Authorization", equalTo(token))
                .withHeader("Nav-Call-Id", equalTo(id))
                .withHeader("Nav-Consumer-Id", equalTo(id))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));
    }

    private void stubFindInstitusjon() {
        stubFor(get(urlEqualTo("/inst2/web/api/institusjon/oppslag/tssEksternId/" + tssEksternId + "?date=" + date))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("Authorization", equalTo(token))
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