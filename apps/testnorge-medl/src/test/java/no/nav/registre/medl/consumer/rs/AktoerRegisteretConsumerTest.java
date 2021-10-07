package no.nav.registre.medl.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AktoerRegisteretConsumerTest {

    @Autowired
    private AktoerRegisteretConsumer aktoerRegisteretConsumer;

    private String miljoe = "t1";

    private List<String> fnrs = new ArrayList<>();

    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";

    @Before
    public void setUp() {
        fnrs.add(fnr1);
        fnrs.add(fnr2);
    }

    @Test
    public void lookupAktoerIdFromFnrIngenFeil() {
        stubForIda();
        stubForAktoerAltFungerer();
        var fnrToAktoer = aktoerRegisteretConsumer.lookupAktoerIdFromFnr(fnrs, miljoe);
        assertTrue(fnrToAktoer.containsKey(fnr1));
        assertTrue(fnrToAktoer.containsKey(fnr2));
        assertEquals("1495709977102", fnrToAktoer.get(fnr1));
        assertEquals("2507809944102", fnrToAktoer.get(fnr2));

    }

    @Test
    public void lookupAktoerIdFromFnrEnFeil() {
        stubForIda();
        stubForAktoerEnFeil();
        var fnrToAktoer = aktoerRegisteretConsumer.lookupAktoerIdFromFnr(fnrs, miljoe);
        assertTrue(fnrToAktoer.containsKey(fnr1));
        assertFalse(fnrToAktoer.containsKey(fnr2));
        assertEquals("1495709977102", fnrToAktoer.get(fnr1));
    }

    @Test
    public void lookupAktoerIdFromFnrInternalErrorIAktoer() {
        stubForIda();
        stubForAktoerGirFeilResponseKode();
        var fnrToAktoer = aktoerRegisteretConsumer.lookupAktoerIdFromFnr(fnrs, miljoe);
        assertTrue(fnrToAktoer.isEmpty());
    }

    @Test
    public void lookupAktoerIdFromFnrIngenResponseFraIda() {
        stubForIdaTomResponse();
        var fnrToAktoer = aktoerRegisteretConsumer.lookupAktoerIdFromFnr(fnrs, miljoe);
        assertTrue(fnrToAktoer.isEmpty());
    }

    private void stubForAktoerGirFeilResponseKode() {
        stubFor(get(urlEqualTo("/aktoer-" + miljoe + "/api/v1/identer?identgruppe=AktoerId&gjeldende=true"))
                .withHeader("Nav-Consumer-Id", equalTo("Synt"))
                .withHeader("Nav-Call-Id", equalTo("Synt"))
                .withHeader("Authorization",
                        equalTo("Bearer abcToken"))
                .withHeader("Nav-Personidenter", equalTo(fnrs.get(0) + ", " + fnrs.get(1)))
                .willReturn(created())
        );
    }

    private void stubForAktoerEnFeil() {
        stubFor(get(urlEqualTo("/aktoer-" + miljoe + "/api/v1/identer?identgruppe=AktoerId&gjeldende=true"))
                .withHeader("Nav-Consumer-Id", equalTo("Synt"))
                .withHeader("Nav-Call-Id", equalTo("Synt"))
                .withHeader("Authorization",
                        equalTo("Bearer abcToken"))
                .withHeader("Nav-Personidenter", equalTo(fnrs.get(0) + ", " + fnrs.get(1)))
                .willReturn(okJson("{\n"
                        + "    \"01010101010\": {\n"
                        + "        \"identer\": [\n"
                        + "            {\n"
                        + "                \"ident\": \"1495709977102\",\n"
                        + "                \"identgruppe\": \"AktoerId\",\n"
                        + "                \"gjeldende\": true\n"
                        + "            }\n"
                        + "        ],\n"
                        + "        \"feilmelding\": null\n"
                        + "    },\n"
                        + "    \"02020202020\": {\n"
                        + "        \"identer\": null,\n"
                        + "        \"feilmelding\": \"Den angitte personidenten finnes ikke\"\n"
                        + "    }\n"
                        + "}"))
        );
    }

    private void stubForAktoerAltFungerer() {
        stubFor(get(urlEqualTo("/aktoer-" + miljoe + "/api/v1/identer?identgruppe=AktoerId&gjeldende=true"))
                .withHeader("Nav-Consumer-Id", equalTo("Synt"))
                .withHeader("Nav-Call-Id", equalTo("Synt"))
                .withHeader("Authorization",
                        equalTo("Bearer abcToken"))
                .withHeader("Nav-Personidenter", equalTo(fnrs.get(0) + ", " + fnrs.get(1)))
                .willReturn(okJson("{\n"
                        + "    \"01010101010\": {\n"
                        + "        \"identer\": [\n"
                        + "            {\n"
                        + "                \"ident\": \"1495709977102\",\n"
                        + "                \"identgruppe\": \"AktoerId\",\n"
                        + "                \"gjeldende\": true\n"
                        + "            }\n"
                        + "        ],\n"
                        + "        \"feilmelding\": null\n"
                        + "    },\n"
                        + "    \"02020202020\": {\n"
                        + "        \"identer\": [\n"
                        + "            {\n"
                        + "                \"ident\": \"2507809944102\",\n"
                        + "                \"identgruppe\": \"AktoerId\",\n"
                        + "                \"gjeldende\": true\n"
                        + "            }\n"
                        + "        ],\n"
                        + "        \"feilmelding\": null\n"
                        + "    }\n"
                        + "}"))
        );
    }

    private void stubForIdaTomResponse() {
        stubFor(get(urlEqualTo("/ida/api/oidctoken_full?ident=dummy&passord=dummy&stack=T")).willReturn(
                ok().withHeader("Content-Type", "text/plain;charset=UTF-8")
        ));
    }

    private void stubForIda() {
        stubFor(get(urlEqualTo("/ida/api/oidctoken_full?ident=dummy&passord=dummy&stack=T")).willReturn(
                okJson("{\n"
                        + "    \"access_token\": \"b431ee3d-d73a-4f81-8d96-aa6de1f278a7\",\n"
                        + "    \"refresh_token\": \"fb39646e-88e3-4078-8a5a-1109f03cccf9\",\n"
                        + "    \"scope\": \"openid\",\n"
                        + "    \"id_token\": \"abcToken\",\n"
                        + "    \"token_type\": \"Bearer\",\n"
                        + "    \"expires_in\": 3599\n"
                        + "}")
                        .withHeader("Content-Type", "text/plain;charset=UTF-8")
        ));
    }
}