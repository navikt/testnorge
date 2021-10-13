package no.nav.registre.endringsmeldinger.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.registre.endringsmeldinger.domain.Endringskoder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.xml.sax.SAXException;

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

import no.nav.registre.endringsmeldinger.consumer.rs.NavEndringsmeldingerSyntetisererenConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.TpsfConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import no.nav.registre.endringsmeldinger.provider.rs.requests.SyntetiserNavEndringsmeldingerRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class EndringsmeldingServiceTest {

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private NavEndringsmeldingerSyntetisererenConsumer syntConsumer;

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private EndringsmeldingService endringsmeldingService;

    private Long avspillergruppeId = 123L;
    private Endringskoder endringskode;
    private int antallMeldinger;
    private SyntetiserNavEndringsmeldingerRequest syntetiserNavEndringsmeldingerRequest;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private List<String> levendeIdenter;

    @Before
    public void setUp() throws IOException, SAXException, ParserConfigurationException {
        endringskode = Endringskoder.TELEFONNUMMER;
        antallMeldinger = 1;

        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(endringskode.getEndringskode(), antallMeldinger);

        var miljoe = "t1";
        syntetiserNavEndringsmeldingerRequest = SyntetiserNavEndringsmeldingerRequest.builder()
                .avspillergruppeId(avspillergruppeId)
                .miljoe(miljoe)
                .antallMeldingerPerEndringskode(antallMeldingerPerEndringskode)
                .build();

        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var xmlStringBuilder = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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
        var input = new ByteArrayInputStream(xmlStringBuilder.getBytes());
        var document = builder.parse(input);

        var rsPureXmlMessageResponse = RsPureXmlMessageResponse.builder()
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

        when(hodejegerenConsumer.getLevende(avspillergruppeId)).thenReturn(levendeIdenter);

        when(tpsfConsumer.sendEndringsmeldingTilTps(any())).thenReturn(rsPureXmlMessageResponse);
    }

    @Test
    @Ignore
    public void shouldOppretteSyntetiskeNavEndringsmeldinger() throws TransformerException {
        var responses = endringsmeldingService.opprettSyntetiskeNavEndringsmeldinger(syntetiserNavEndringsmeldingerRequest);

        verify(syntConsumer).getSyntetiserteNavEndringsmeldinger(endringskode.getEndringskode(), antallMeldinger);
        verify(hodejegerenConsumer).getLevende(avspillergruppeId);
        verify(tpsfConsumer).sendEndringsmeldingTilTps(any());

        assertThat(responses.get(0).getXml(), containsString(fnr1));
    }
}