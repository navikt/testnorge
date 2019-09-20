package no.nav.registre.tss.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;
import wiremock.com.google.common.io.Resources;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.TssSyntetisererenConsumer;
import no.nav.registre.tss.consumer.rs.responses.Response910;
import no.nav.registre.tss.consumer.rs.responses.TssSyntMessage;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.provider.rs.requests.SyntetiserTssRequest;

@RunWith(MockitoJUnitRunner.class)
public class TssServiceTest {

    private static final int MIN_ALDER = 25;
    private static final int MAX_ALDER = 70;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private TssSyntetisererenConsumer tssSyntetisererenConsumer;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private TssService tssService;

    private String koeNavn = "testKoe";
    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String fnr1 = "23026325811";
    private String navn1 = "GLOBUS IHERDIG STRAFFET";
    private String fnr2 = "08016325431";
    private String navn2 = "BORD OPPSTEMT ELEKTRONISK";

    @Test
    public void shouldHenteIdenter() throws IOException {
        URL resource = Resources.getResource("status_quo_test.json");
        JsonNode jsonNode = new ObjectMapper().readValue(resource, JsonNode.class);
        JsonNode value1 = jsonNode.findValue(fnr1);
        JsonNode value2 = jsonNode.findValue(fnr2);
        Map<String, JsonNode> identerMedStatusQuo = new HashMap<>();
        identerMedStatusQuo.put(fnr1, value1);
        identerMedStatusQuo.put(fnr2, value2);

        SyntetiserTssRequest syntetiserTssRequest = SyntetiserTssRequest.builder()
                .avspillergruppeId(avspillergruppeId)
                .miljoe(miljoe)
                .antallNyeIdenter(antallNyeIdenter)
                .build();

        when(hodejegerenConsumer.getStatusQuo(avspillergruppeId, miljoe, antallNyeIdenter, MIN_ALDER, MAX_ALDER)).thenReturn(identerMedStatusQuo);

        List<Person> identer = tssService.hentIdenter(syntetiserTssRequest);

        assertThat(identer.get(0).getFnr(), is(fnr1));
        assertThat(identer.get(0).getNavn(), is(navn1));
        assertThat(identer.get(1).getFnr(), is(fnr2));
        assertThat(identer.get(1).getNavn(), is(navn2));

        verify(hodejegerenConsumer).getStatusQuo(avspillergruppeId, miljoe, antallNyeIdenter, MIN_ALDER, MAX_ALDER);
    }

    @Test
    public void shouldOppretteSyntetiskeRutiner() throws IOException {
        URL resource = Resources.getResource("syntetiske_rutiner_flatfil.json");
        List<String> forventetResultat = new ObjectMapper().readValue(resource, new TypeReference<List<String>>() {
        });

        resource = Resources.getResource("syntetiske_rutiner.json");
        Map<String, List<TssSyntMessage>> syntetiskeMeldinger = new ObjectMapper().readValue(resource, new TypeReference<Map<String, List<TssSyntMessage>>>() {
        });

        Person person1 = new Person(fnr1, navn1);
        Person person2 = new Person(fnr2, navn2);
        List<Person> personer = new ArrayList<>(Arrays.asList(person1, person2));

        LocalDate birthdate = LocalDate.of(1963, 2, 23);
        int alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(person1.getAlder(), is(alder));

        birthdate = LocalDate.of(1963, 1, 8);
        alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(person2.getAlder(), is(alder));

        when(tssSyntetisererenConsumer.hentSyntetiskeTssRutiner(anyList())).thenReturn(syntetiskeMeldinger);

        List<String> syntetiskeRutiner = tssService.opprettSyntetiskeTssRutiner(personer);

        assertThat(syntetiskeRutiner.get(0), equalTo(forventetResultat.get(0)));
        assertThat(syntetiskeRutiner.get(1), equalTo(forventetResultat.get(1)));
    }

    @Test
    public void shouldSendeTilTss() {
        List<String> syntetiskeMeldinger = new ArrayList<>(Arrays.asList("Some melding", "Some other melding"));
        tssService.sendTilTss(syntetiskeMeldinger, koeNavn);

        verify(jmsTemplate).convertAndSend("queue:///" + koeNavn + "?targetClient=1", "Some melding");
        verify(jmsTemplate).convertAndSend("queue:///" + koeNavn + "?targetClient=1", "Some other melding");
    }

    @Test
    public void shouldSendOgMotta910RutinerFraTss() throws JMSException {
        String fnr = "24107626502";
        when(hodejegerenConsumer.getLevende(avspillergruppeId)).thenReturn(Collections.singletonList(fnr));

        Message message = mock(Message.class);
        when(message.getBody(String.class)).thenReturn(getSampleResponseText());
        when(jmsTemplate.sendAndReceive(eq("queue:///" + koeNavn + "?targetClient=1"), any())).thenReturn(message);

        Map<String, Response910> response = tssService.sendOgMotta910RutineFraTss(avspillergruppeId, 1, koeNavn);

        verify(jmsTemplate).sendAndReceive(eq("queue:///" + koeNavn + "?targetClient=1"), any());

        assertThat(response.get(fnr).getResponse110().get(0).getIdOff(), equalTo(fnr));
        assertThat(response.get(fnr).getResponse111().get(0).getIdAlternativ(), equalTo(fnr));
        assertThat(response.get(fnr).getResponse111().get(1).getIdAlternativ(), equalTo("587977648"));
        assertThat(response.get(fnr).getResponse125().get(0).getIdTSSEkstern(), equalTo("80900100006"));
    }

    @Test
    public void shouldSendOgMotta910RutineFraTss() throws JMSException {
        String fnr = "24107626502";

        Message message = mock(Message.class);
        when(message.getBody(String.class)).thenReturn(getSampleResponseText());
        when(jmsTemplate.sendAndReceive(eq("queue:///" + koeNavn + "?targetClient=1"), any())).thenReturn(message);

        Response910 response = tssService.sendOgMotta910RutineFraTss(fnr, koeNavn);

        verify(jmsTemplate).sendAndReceive(eq("queue:///" + koeNavn + "?targetClient=1"), any());

        assertThat(response.getResponse110().get(0).getIdOff(), equalTo(fnr));
        assertThat(response.getResponse111().get(0).getIdAlternativ(), equalTo(fnr));
        assertThat(response.getResponse111().get(1).getIdAlternativ(), equalTo("587977648"));
        assertThat(response.getResponse125().get(0).getIdTSSEkstern(), equalTo("80900100006"));
    }

    private String getSampleResponseText() {
        return "COB                00                                                                                                                                            "
                + "                                                                      910           24107626502FNR                                                     "
                + "                                                                                                                                                       "
                + "                                                                    11024107626502FNR LE  Lege                          20190919        ETAT VRIEN     "
                + "                         NO                                      GYLDGyldig                     SYNTORK     20190919121811124107626502FNR Norsk fødselsnummer"
                + "                     20190919        JSYNTORK     201909191218                                                                                         "
                + "               111587977648  HPR HPR-nummer                              20190919        JSYNTORK     201909191218                                     "
                + "                                                                   12501                                                                               "
                + "     20190919        J80900100006           SYNTORK     201909191218";
    }
}