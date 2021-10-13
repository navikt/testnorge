package no.nav.registre.endringsmeldinger.consumer.rs;

import no.nav.registre.endringsmeldinger.service.Endringskoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class NavEndringsmeldingerSyntetisererenConsumerTest {

    @Autowired
    private NavEndringsmeldingerSyntetisererenConsumer syntetisererenConsumer;

    private String endringskode = Endringskoder.TELEFONNUMMER.getEndringskode();
    private int antallMeldinger = 1;

    @Test
    public void shouldGetSyntetiserteMeldinger() throws TransformerException {
        stubBisysSyntetisererenConsumer();

        var syntetiserteNavEndringsmeldinger = syntetisererenConsumer.getSyntetiserteNavEndringsmeldinger(endringskode, antallMeldinger).getBody();

        var tf = TransformerFactory.newInstance();
        var transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        var stringWriter = new StringWriter();
        transformer.transform(new DOMSource(syntetiserteNavEndringsmeldinger.get(0)), new StreamResult(stringWriter));
        var output = stringWriter.toString().replaceAll("\n|\r", "");

        assertThat(output, containsString("69328480"));
    }

    private void stubBisysSyntetisererenConsumer() {
        stubFor(get(urlEqualTo("/synthdata-nav-endringsmeldinger/api/v1/generate/nav/" + endringskode + "?numToGenerate=" + antallMeldinger))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>"
                                + "<sfePersonData>"
                                + "    <sfeAjourforing>"
                                + "        <systemInfo>"
                                + "            <kilde>PP01</kilde>"
                                + "            <brukerID>Srvpselv</brukerID>"
                                + "        </systemInfo>"
                                + "        <endreTelefon>"
                                + "            <offentligIdent></offentligIdent>"
                                + "            <typeTelefon>MOBI</typeTelefon>"
                                + "            <telefonNr>69328480</telefonNr>"
                                + "            <datoTelefon>2017-10-30</datoTelefon>"
                                + "        </endreTelefon>"
                                + "    </sfeAjourforing>"
                                + "</sfePersonData>\"]")));
    }
}