package no.nav.registre.endringsmeldinger.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.endringsmeldinger.consumer.rs.HodejegerenConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.NavEndringsmeldingerSyntetisererenConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.TpsfConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import no.nav.registre.endringsmeldinger.provider.rs.requests.SyntetiserNavEndringsmeldingerRequest;

@RunWith(MockitoJUnitRunner.class)
public class EndringsmeldingServiceTest {

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private NavEndringsmeldingerSyntetisererenConsumer syntConsumer;

    @Mock
    private TpsfConsumer tpsfConsumer;

    @Mock
    private Random rand;

    @InjectMocks
    private EndringsmeldingService endringsmeldingService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private Map<String, Integer> antallMeldingerPerEndringskode;
    private Endringskoder endringskode;
    private int antallMeldinger;
    private SyntetiserNavEndringsmeldingerRequest syntetiserNavEndringsmeldingerRequest;
    private Document document;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private List<String> levendeIdenter;

    @Before
    public void setUp() throws IOException, SAXException, ParserConfigurationException {
        endringskode = Endringskoder.TELEFONNUMMER;
        antallMeldinger = 1;

        antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(endringskode.getEndringskode(), antallMeldinger);

        syntetiserNavEndringsmeldingerRequest = SyntetiserNavEndringsmeldingerRequest.builder()
                .avspillergruppeId(avspillergruppeId)
                .miljoe(miljoe)
                .antallMeldingerPerEndringskode(antallMeldingerPerEndringskode)
                .build();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String xmlStringBuilder = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<sfePersonData>\n"
                + "    <sfeAjourforing>\n"
                + "        <systemInfo>\n"
                + "            <kilde>PP01</kilde>\n"
                + "            <brukerID>Srvpselv</brukerID>\n"
                + "        </systemInfo>\n"
                + "        <endreTelefon>\n"
                + "            <offentligIdent></offentligIdent>\n"
                + "            <typeTelefon>MOBI</typeTelefon>\n"
                + "            <telefonNr>69328480</telefonNr>\n"
                + "            <datoTelefon>2017-10-30</datoTelefon>\n"
                + "        </endreTelefon>\n"
                + "    </sfeAjourforing>\n"
                + "</sfePersonData>");
        ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.getBytes());
        document = builder.parse(input);

        RsPureXmlMessageResponse rsPureXmlMessageResponse = RsPureXmlMessageResponse.builder()
                .xml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<sfePersonData>\n"
                        + "    <sfeAjourforing>\n"
                        + "        <systemInfo>\n"
                        + "            <kilde>PP01</kilde>\n"
                        + "            <brukerID>Srvpselv</brukerID>\n"
                        + "        </systemInfo>\n"
                        + "        <endreTelefon>\n"
                        + "            <offentligIdent>" + fnr1 + "</offentligIdent>\n"
                        + "            <typeTelefon>MOBI</typeTelefon>\n"
                        + "            <telefonNr>69328480</telefonNr>\n"
                        + "            <datoTelefon>2017-10-30</datoTelefon>\n"
                        + "        </endreTelefon>\n"
                        + "    </sfeAjourforing>\n"
                        + "    <sfeTilbakeMelding>\n"
                        + "        <svarStatus>\n"
                        + "            <returStatus>00</returStatus>\n"
                        + "            <returMelding></returMelding>\n"
                        + "            <utfyllendeMelding></utfyllendeMelding>\n"
                        + "        </svarStatus>\n"
                        + "    </sfeTilbakeMelding>\n"
                        + "</sfePersonData>")
                .build();

        levendeIdenter = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        when(syntConsumer.getSyntetiserteNavEndringsmeldinger(endringskode.getEndringskode(), antallMeldinger))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(document)));

        when(hodejegerenConsumer.finnLevendeIdenter(avspillergruppeId)).thenReturn(levendeIdenter);

        when(tpsfConsumer.sendEndringsmeldingTilTps(any())).thenReturn(rsPureXmlMessageResponse);
    }

    @Test
    public void shouldOppretteSyntetiskeNavEndringsmeldinger() throws TransformerException {
        List<RsPureXmlMessageResponse> responses = endringsmeldingService.opprettSyntetiskeNavEndringsmeldinger(syntetiserNavEndringsmeldingerRequest);

        verify(syntConsumer).getSyntetiserteNavEndringsmeldinger(endringskode.getEndringskode(), antallMeldinger);
        verify(hodejegerenConsumer).finnLevendeIdenter(avspillergruppeId);
        verify(tpsfConsumer).sendEndringsmeldingTilTps(any());

        assertThat(responses.get(0).getXml(), containsString(fnr1));
    }
}