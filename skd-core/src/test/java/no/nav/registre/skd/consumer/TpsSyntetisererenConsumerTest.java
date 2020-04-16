package no.nav.registre.skd.consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static no.nav.registre.skd.testutils.ResourceUtils.getResourceFileContent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import no.nav.registre.skd.testutils.AssertionUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class TpsSyntetisererenConsumerTest {

    @Autowired
    private TpsSyntetisererenConsumer consumer;

    /**
     * Tester om metoden bygger korrekt URI og queryParam når den konsumerer Tps Synt.
     */
    @Test
    public void testRequestKonsumeringAvTpsSynt() {
        var endringskode = "0211";
        var antallMeldinger = 1;

        stubHentTomEndringsmelding(endringskode, antallMeldinger);

        consumer.getSyntetiserteSkdmeldinger(endringskode, antallMeldinger);
    }

    /**
     * Tester at alle navnene i forventet responsmelding fra TPS Syntetisereren
     * blir med i java-objektet RsMeldingstype1Felter.
     */
    @Test
    public void shouldDeserialiseAllFieldsInTheResponse() throws InvocationTargetException, IllegalAccessException {
        var endringskode = "0211";
        var antallMeldinger = 1;
        stubHentEndringsmelding(endringskode, antallMeldinger);

        var skdmeldinger = consumer.getSyntetiserteSkdmeldinger(endringskode, antallMeldinger);

        var ignoredFields = Arrays.asList("getSaksid", "getEmbete", "getSakstype",
                "getVedtaksdato", "getInternVergeid", "getVergeFnrDnr", "getVergetype",
                "getMandattype", "getMandatTekst", "getReserverFramtidigBruk");
        AssertionUtils.assertAllFieldsNotNull(skdmeldinger.get(0), ignoredFields);
    }

    private void stubHentTomEndringsmelding(
            String endringskode,
            int antallMeldinger
    ) {
        stubFor(get(urlEqualTo("/tpssynt/api/v1/generate/tps/" + endringskode + "?numToGenerate=" + antallMeldinger))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                ));
    }

    private void stubHentEndringsmelding(
            String endringskode,
            int antallMeldinger
    ) {
        stubFor(get(urlEqualTo("/tpssynt/api/v1/generate/tps/" + endringskode + "?numToGenerate=" + antallMeldinger))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody(getResourceFileContent("__files/tpssynt/tpsSynt_NotNullFields_Response.json"))
                ));
    }
}