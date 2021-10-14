package no.nav.registre.endringsmeldinger.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.transform.TransformerException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.endringsmeldinger.consumer.rs.TpsfConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.requests.SendTilTpsRequest;
import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import no.nav.registre.endringsmeldinger.provider.rs.requests.GenererKontonummerRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestdataServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private TestdataService testdataService;

    @Captor
    private ArgumentCaptor<SendTilTpsRequest> captor;

    private String koeNavn = "testkoe";
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private String meldingFnr1;
    private String meldingFnr2;
    private String kontonummer = "03030303030";
    private String miljoe = "t1";
    private List<String> identer;
    private GenererKontonummerRequest genererKontonummerRequest;
    private RsPureXmlMessageResponse rsPureXmlMessageResponseSuccess;
    private RsPureXmlMessageResponse rsPureXmlMessageResponseFail;

    @Before
    public void setUp() {
        var dato = LocalDate.now().toString();
        meldingFnr1 = ""
                + "<sfePersonData>\n"
                + "    <sfeAjourforing>\n"
                + "        <systemInfo>\n"
                + "            <kilde>TPSF</kilde>\n"
                + "            <brukerID>ORK</brukerID>\n"
                + "        </systemInfo>\n"
                + "        <endreGironrNorsk>\n"
                + "            <offentligIdent>" + fnr1 + "</offentligIdent>\n"
                + "            <giroNrNorsk>" + kontonummer + "</giroNrNorsk>\n"
                + "            <datoGiroNrNorsk>" + dato + "</datoGiroNrNorsk>\n"
                + "        </endreGironrNorsk>\n"
                + "    </sfeAjourforing>\n"
                + "</sfePersonData>";

        meldingFnr2 = ""
                + "<sfePersonData>\n"
                + "    <sfeAjourforing>\n"
                + "        <systemInfo>\n"
                + "            <kilde>TPSF</kilde>\n"
                + "            <brukerID>ORK</brukerID>\n"
                + "        </systemInfo>\n"
                + "        <endreGironrNorsk>\n"
                + "            <offentligIdent>" + fnr2 + "</offentligIdent>\n"
                + "            <giroNrNorsk>" + kontonummer + "</giroNrNorsk>\n"
                + "            <datoGiroNrNorsk>" + dato + "</datoGiroNrNorsk>\n"
                + "        </endreGironrNorsk>\n"
                + "    </sfeAjourforing>\n"
                + "</sfePersonData>";

        rsPureXmlMessageResponseSuccess = new RsPureXmlMessageResponse();
        rsPureXmlMessageResponseSuccess.setXml(""
                + "<sfePersonData>\n"
                + "    <sfeAjourforing>\n"
                + "        <systemInfo>\n"
                + "            <kilde>TPSF</kilde>\n"
                + "            <brukerID>ORK</brukerID>\n"
                + "        </systemInfo>\n"
                + "        <endreGironrNorsk>\n"
                + "            <offentligIdent>" + fnr1 + "</offentligIdent>\n"
                + "            <giroNrNorsk>" + kontonummer + "</giroNrNorsk>\n"
                + "            <datoGiroNrNorsk>" + dato + "</datoGiroNrNorsk>\n"
                + "        </endreGironrNorsk>\n"
                + "    </sfeAjourforing>\n"
                + "    <sfeTilbakeMelding>\n"
                + "        <svarStatus>\n"
                + "            <returStatus>00</returStatus>\n"
                + "            <returMelding></returMelding>\n"
                + "            <utfyllendeMelding></utfyllendeMelding>\n"
                + "        </svarStatus>\n"
                + "    </sfeTilbakeMelding>\n"
                + "</sfePersonData>");

        rsPureXmlMessageResponseFail = new RsPureXmlMessageResponse();
        rsPureXmlMessageResponseFail.setXml(""
                + "<sfePersonData>\n"
                + "    <sfeAjourforing>\n"
                + "        <systemInfo>\n"
                + "            <kilde>TPSF</kilde>\n"
                + "            <brukerID>ORK</brukerID>\n"
                + "        </systemInfo>\n"
                + "        <endreGironrNorsk>\n"
                + "            <offentligIdent>" + fnr2 + "</offentligIdent>\n"
                + "            <giroNrNorsk>" + kontonummer + "</giroNrNorsk>\n"
                + "            <datoGiroNrNorsk>" + dato + "</datoGiroNrNorsk>\n"
                + "        </endreGironrNorsk>\n"
                + "    </sfeAjourforing>\n"
                + "    <sfeTilbakeMelding>\n"
                + "        <svarStatus>\n"
                + "            <returStatus>08</returStatus>\n"
                + "            <returMelding>T6200011</returMelding>\n"
                + "            <utfyllendeMelding>Ikke aktivt fødselsnummer</utfyllendeMelding>\n"
                + "        </svarStatus>\n"
                + "    </sfeTilbakeMelding>\n"
                + "</sfePersonData>");
    }

    @Test
    public void shouldGenerereKontonummerAndSendToTpsSuccess() throws TransformerException {
        setUpSuccess();
        when(tpsfConsumer.sendEndringsmeldingTilTps(any())).thenReturn(rsPureXmlMessageResponseSuccess);
        var response = testdataService.genererKontonummerOgSendTilTps(koeNavn, genererKontonummerRequest);

        verify(tpsfConsumer).sendEndringsmeldingTilTps(captor.capture());

        assertThat(captor.getValue().getMelding(), equalTo(meldingFnr1.replaceAll("\\r?\\n", "").replaceAll(" ", "")));
        assertThat(response.get(0).getXml(), equalTo(rsPureXmlMessageResponseSuccess.getXml()));
    }

    @Test
    public void shouldGenerereKontonummerAndSendToTpsFailure() throws TransformerException {
        setUpFailure();
        when(tpsfConsumer.sendEndringsmeldingTilTps(any())).thenReturn(rsPureXmlMessageResponseFail);
        var response = testdataService.genererKontonummerOgSendTilTps(koeNavn, genererKontonummerRequest);

        verify(tpsfConsumer).sendEndringsmeldingTilTps(captor.capture());

        assertThat(captor.getValue().getMelding(), equalTo(meldingFnr2.replaceAll("\\r?\\n", "").replaceAll(" ", "")));
        assertThat(response.get(0).getXml(), equalTo(rsPureXmlMessageResponseFail.getXml()));
    }

    private void setUpSuccess() {
        identer = new ArrayList<>(Collections.singletonList(fnr1));
        genererKontonummerRequest = GenererKontonummerRequest.builder()
                .identer(identer)
                .kontonummer(kontonummer)
                .miljoe(miljoe)
                .build();
    }

    private void setUpFailure() {
        identer = new ArrayList<>(Collections.singletonList(fnr2));
        genererKontonummerRequest = GenererKontonummerRequest.builder()
                .identer(identer)
                .kontonummer(kontonummer)
                .miljoe(miljoe)
                .build();
    }
}