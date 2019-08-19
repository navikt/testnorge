package no.nav.registre.inntekt.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.forbidden;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.inntekt.domain.RsInntekt;
import no.nav.registre.inntekt.domain.RsPerson;
import no.nav.registre.inntekt.testUtils.InntektGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@Slf4j
public class InntektstubConsumerTest {

    @Autowired
    private InntektstubConsumer inntektstubConsumer;

    @Test
    public void leggTilInntektTest() {
        stubForToken();
        stubForInntektstub();

        RsInntekt inntekt = InntektGenerator.genererInntekt(1490);

        Map<String, List<RsInntekt>> inntekter = new HashMap<>();
        inntekter.put("10128400000", Collections.singletonList(inntekt));

        Map<String, List<RsInntekt>> feilet = inntektstubConsumer.leggInntekterIInntektstub(inntekter);

        assertNull(feilet);
    }

    @Test
    public void leggTilInntektTestIngenToken() {
        stubForNoToken();
        stubForInntektstubForbidden();

        RsInntekt inntekt = InntektGenerator.genererInntekt(1490);

        Map<String, List<RsInntekt>> inntekter = new HashMap<>();
        inntekter.put("10128400000", Collections.singletonList(inntekt));

        Map<String, List<RsInntekt>> feilet = null;
        try {
            feilet = inntektstubConsumer.leggInntekterIInntektstub(inntekter);
        } catch (HttpClientErrorException e) {
            log.info(e.getMessage(), e);
        }
        assertNull(feilet);
    }

    @Test
    public void hentEksisterendeIdenterFraInntektstubTest() {
        String fnr = "01010101010";
        stubForToken();
        stubHentEksisterendeIdenter(fnr);

        List<RsPerson> identer = inntektstubConsumer.hentEksisterendeIdenter();

        assertThat(identer.get(0).getFoedselsnummer(), Matchers.equalTo(fnr));
    }

    private void stubForToken() {
        stubFor(get(urlPathMatching("/inntektstub/api/v1/user"))
                .willReturn(ok()
                        .withHeader("Set-Cookie", "XSRF-TOKEN=2913819-123912389-12391239; asdas")));
    }

    private void stubForNoToken() {
        stubFor(get(urlPathMatching("/inntektstub/api/v1/user"))
                .willReturn(ok()));
    }

    private void stubForInntektstub() {
        stubFor(post(urlPathMatching("/inntektstub/api/v1/personer/inntekt"))
                .willReturn(ok()));
    }

    private void stubForInntektstubForbidden() {
        stubFor(post(urlPathMatching("/inntektstub/api/v1/personer/inntekt"))
                .willReturn(forbidden()));
    }

    private void stubHentEksisterendeIdenter(String fnr) {
        stubFor(get(urlPathEqualTo("/inntektstub/api/v1/personer"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\n"
                                + "        \"foedselsnummer\": \"" + fnr + "\",\n"
                                + "        \"tags\": [\n"
                                + "            \"BISYS\",\n"
                                + "            \"Sluttvederlag\",\n"
                                + "            \"Arena\",\n"
                                + "            \"Heltidsstilling\"\n"
                                + "        ]\n"
                                + "    }]")));
    }
}