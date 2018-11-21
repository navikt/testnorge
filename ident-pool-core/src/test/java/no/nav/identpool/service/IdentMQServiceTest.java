package no.nav.identpool.service;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import javax.xml.bind.JAXB;

import no.nav.identpool.test.mockito.MockitoExtension;
import no.nav.tps.ctg.m201.domain.PersondataFraTpsS201;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.identpool.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ajourhold.mq.factory.MessageQueueFactory;
import no.nav.tps.ctg.m201.domain.PersondataFraTpsM201;
import no.nav.tps.ctg.m201.domain.TpsPersonData;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tester sjekking av identer mot TPS (MQ)")
class IdentMQServiceTest {

    @Mock
    private MessageQueueFactory messageQueueFactory;

    @Mock
    private MessageQueue messageQueue;

    @Test
    @DisplayName("Skal ta i bruk kø uten cache")
    void shoudThrow() throws Exception {
        TpsPersonData personData = JAXB.unmarshal(fetchTestXml(), TpsPersonData.class);
        List<String> idents = extractIdents(personData);
        mockMqError();
        mockBackupMq(personData);

        Map<String, Boolean> identerFinnes = sendToTps(idents);

        assertResponseOk(personData, identerFinnes);
    }

    @Test
    @DisplayName("Sjekker at mapping av om identer finnes i TPS går ok")
    void finnesITps() throws Exception {
        TpsPersonData personData = JAXB.unmarshal(fetchTestXml(), TpsPersonData.class);
        List<String> idents = extractIdents(personData);

        mockMqSuccess(personData);

        Map<String, Boolean> identerFinnes = sendToTps(idents);

        assertResponseOk(personData, identerFinnes);
    }

    private void assertResponseOk(TpsPersonData personData, Map<String, Boolean> identerFinnes) {
        List<PersondataFraTpsM201.AFnr.EFnr> eFnrs = personData.getTpsSvar().getPersonDataM201().getAFnr().getEFnr();
        eFnrs.forEach(eFnr -> {
            Boolean finnes = eFnr.getSvarStatus() == null || !"08".equals(eFnr.getSvarStatus().getReturStatus());
            assertEquals(identerFinnes.get(eFnr.getFnr()), finnes);
        });
    }

    private Map<String, Boolean> sendToTps(List<String> idents) {
        IdentMQService identMQService = new IdentMQService(messageQueueFactory);
        return identMQService.checkInTps(asList("t1", "t4"), idents);
    }

    private List<String> extractIdents(TpsPersonData personData) {
        return personData.getTpsSvar().getPersonDataM201().getAFnr().getEFnr().stream()
                .map(PersondataFraTpsS201::getFnr)
                .collect(Collectors.toList());
    }

    private void mockMqSuccess(TpsPersonData personData) throws JMSException {
        StringWriter stringWriter = new StringWriter();
        JAXB.marshal(personData, stringWriter);
        when(messageQueue.sendMessage(anyString())).thenReturn(stringWriter.toString());
        when(messageQueueFactory.createMessageQueue(anyString())).thenReturn(messageQueue);
    }

    private void mockMqError() throws JMSException {
        when(messageQueueFactory.createMessageQueue(anyString())).thenThrow(new JMSException("JMS Error"));
    }

    private void mockBackupMq(TpsPersonData personData) throws JMSException {
        StringWriter stringWriter = new StringWriter();
        JAXB.marshal(personData, stringWriter);
        when(messageQueue.sendMessage(anyString())).thenReturn(stringWriter.toString());
        when(messageQueueFactory.createMessageQueueIgnoreCache(anyString())).thenReturn(messageQueue);
    }

    private static InputStream fetchTestXml() throws IOException {
        return (new ClassPathResource("mq/persondataSuccess.xml")).getInputStream();
    }
}
