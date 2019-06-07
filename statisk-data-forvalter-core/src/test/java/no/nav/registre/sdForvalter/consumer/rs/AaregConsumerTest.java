package no.nav.registre.sdForvalter.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import no.nav.registre.sdForvalter.database.model.AaregModel;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AaregConsumerTest {

    private final String environment = "t1";

    @Autowired
    private AaregConsumer aaregConsumer;

    @Test
    public void sendGyldigeTilAdapter() {

        Set<AaregModel> data = new HashSet<>();
        data.add(new AaregModel("123", 345L));

        stubAareg();

        Map<String, String> statusMap = aaregConsumer.send(data, environment);
        assertTrue(statusMap.isEmpty());
    }

    private void stubAareg() {
        stubFor(post(urlEqualTo("/testnorge-aareg/api/v1/syntetisering/sendTilAareg?fyllUtArbeidsforhold=true"))
                .withRequestBody(equalToJson(
                        "[{"
                                + "\"arbeidsforhold\": {"
                                + "\"arbeidsgiver\": {"
                                + "\"aktoertype\": \"ORG\","
                                + "\"orgnummer\": \"345\""
                                + "},"
                                + "\"arbeidstaker\": {"
                                + "\"aktoertype\": \"PERS\","
                                + "\"identtype\": \"FNR\","
                                + "\"ident\": \"123\""
                                + "}"
                                + "},"
                                + "\"environments\": [\"t1\"]"
                                + "}]"
                ))
                .willReturn(
                        okJson(
                                "{"
                                        + "\"identerLagretIStub\": [\"123\"],"
                                        + "\"identerLagretIAareg\": [\"123\"],"
                                        + "\"identerSomIkkeKunneLagresIAareg\": {"
                                        + "}"
                                        + "}"
                        )
                ));
    }

    @Test
    public void sendNoeFeil() {
        Set<AaregModel> data = new HashSet<>();
        data.add(new AaregModel("123", 0));

        stubFeilAareg();
        Map<String, String> statusMap = aaregConsumer.send(data, environment);
        assertFalse(statusMap.isEmpty());
    }

    private void stubFeilAareg() {
        stubFor(post(urlEqualTo("/testnorge-aareg/api/v1/syntetisering/sendTilAareg?fyllUtArbeidsforhold=true"))
                .withRequestBody(equalToJson(
                        "[{"
                                + "\"arbeidsforhold\": {"
                                + "\"arbeidsgiver\": {"
                                + "\"aktoertype\": \"ORG\","
                                + "\"orgnummer\": \"0\""
                                + "},"
                                + "\"arbeidstaker\": {"
                                + "\"aktoertype\": \"PERS\","
                                + "\"identtype\": \"FNR\","
                                + "\"ident\": \"123\""
                                + "}"
                                + "},"
                                + "\"environments\": [\"t1\"]"
                                + "}]"
                ))
                .willReturn(
                        okJson(
                                "{"
                                        + "\"identerLagretIStub\": [],"
                                        + "\"identerLagretIAareg\": [],"
                                        + "\"identerSomIkkeKunneLagresIAareg\": {"
                                        + "\"123\": \"feil\""
                                        + "}"
                                        + "}"
                        )
                ));
    }

    @Test
    public void hentArbeidsforhold() {
        Set<String> fnrs = new HashSet<>();
        fnrs.add("123");
        fnrs.add("456");

        stubAaregStubFound();
        stubAaregStubNotFound();

        Set<String> resultFnr = aaregConsumer.finnPersonerUtenArbeidsforhold(fnrs, environment);

        assertNotNull(resultFnr);
        assertEquals(1, resultFnr.size());
        assertTrue(resultFnr.contains("456"));
    }

    private void stubAaregStubFound() {
        stubFor(get(urlEqualTo("/testnorge-aaregstub/api/v1/hentArbeidsforholdFraAareg?ident=123&miljoe=t1"))
                .willReturn(
                        okJson("[{\"fnr\": \"123\"}]")
                ));
    }

    private void stubAaregStubNotFound() {
        stubFor(get(urlEqualTo("/testnorge-aaregstub/api/v1/hentArbeidsforholdFraAareg?ident=456&miljoe=t1"))
                .willReturn(
                        okJson("[]")
                ));
    }
}