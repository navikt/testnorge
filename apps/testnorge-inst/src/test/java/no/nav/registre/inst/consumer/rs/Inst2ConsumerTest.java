package no.nav.registre.inst.consumer.rs;

import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.registre.inst.domain.InstitusjonsoppholdV2;
import no.nav.registre.inst.exception.UgyldigIdentResponseException;
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
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.registre.inst.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureWireMock(port = 0)
public class Inst2ConsumerTest {

    @Autowired
    private Inst2Consumer inst2Consumer;

    private final String id = "test";
    private final String fnr1 = "01010101010";
    private final String token = "Bearer abc";
    private final String miljoe = "q2";

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

    @Test(expected = UgyldigIdentResponseException.class)
    public void shouldThrowUgyldigIdentResponseExceptionOnBadRequest() {
        stubGetInstitusjonsoppholdWithBadRequest();

        inst2Consumer.hentInstitusjonsoppholdFraInst2(token, id, id, miljoe, fnr1);
    }

    @Test
    public void shouldAddInstitusjonsoppholdTilInst2() {
        var institusjonsopphold = InstitusjonsoppholdV2.builder().build();

        stubAddInstitusjonsopphold();

        var oppholdResponse = inst2Consumer.leggTilInstitusjonsoppholdIInst2(token, id, id, miljoe, institusjonsopphold);

        assertThat(oppholdResponse.getStatus(), is(HttpStatus.CREATED));
    }

    private void stubGetInstitusjonsopphold() {
        stubFor(get(urlPathMatching("(.*)/v1/institusjonsopphold/person"))
                .withQueryParam("environments", equalTo(miljoe))
                .withHeader("norskident", equalTo(fnr1))
                .withHeader("accept", equalTo("*/*"))
                .withHeader("Authorization", equalTo(token))
                .withHeader("Nav-Call-Id", equalTo(id))
                .withHeader("Nav-Consumer-Id", equalTo(id))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("institusjonsmeldingResponse.json"))));
    }

    private void stubGetInstitusjonsoppholdWithBadRequest() {
        stubFor(get(urlPathMatching("(.*)/v1/institusjonsopphold/person"))
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
}