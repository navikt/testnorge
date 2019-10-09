package no.nav.registre.tss.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.nav.registre.tss.consumer.rs.response.Response910;

@RunWith(MockitoJUnitRunner.class)
public class JmsServiceTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private JmsService jmsService;

    private String koeNavn = "testKoe";

    @Test
    public void shouldSendeTilTss() {
        List<String> syntetiskeMeldinger = new ArrayList<>(Arrays.asList("Some melding", "Some other melding"));
        jmsService.sendTilTss(syntetiskeMeldinger, koeNavn);

        verify(jmsTemplate).convertAndSend("queue:///" + koeNavn + "?targetClient=1", "Some melding");
        verify(jmsTemplate).convertAndSend("queue:///" + koeNavn + "?targetClient=1", "Some other melding");
    }

    @Test
    public void shouldSendOgMotta910RutinerFraTss() throws JMSException {
        String fnr = "24107626502";

        Message message = mock(Message.class);
        when(message.getBody(String.class)).thenReturn(getSampleResponseText());
        when(jmsTemplate.sendAndReceive(eq("queue:///" + koeNavn + "?targetClient=1"), any())).thenReturn(message);

        Map<String, Response910> response = jmsService.sendOgMotta910RutineFraTss(Collections.singletonList(fnr), koeNavn);

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

        Response910 response = jmsService.sendOgMotta910RutineFraTss(fnr, koeNavn);

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