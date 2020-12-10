package no.nav.identpool.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import javax.xml.bind.JAXB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.mq.consumer.MessageQueue;
import no.nav.identpool.mq.factory.MessageQueueFactory;
import no.nav.identpool.service.support.QueueContext;
import no.nav.identpool.test.mockito.MockitoExtension;
import no.nav.tps.ctg.m201.domain.PersondataFraTpsM201;
import no.nav.tps.ctg.m201.domain.PersondataFraTpsS201;
import no.nav.tps.ctg.m201.domain.TpsPersonData;

@Disabled
@ExtendWith(MockitoExtension.class)
@DisplayName("Tester sjekking av identer mot TPS (MQ)")
class IdentTpsServiceTest {

    @Mock
    private MessageQueueFactory messageQueueFactory;

    @Mock
    private MessageQueue messageQueue;

    @Mock
    private QueueContext queueContext;

    @InjectMocks
    private IdentTpsService identTpsService;

    @BeforeEach
    void setup() {
        mockQueueContext();
    }

    @Test
    @DisplayName("Skal ta i bruk kø uten cache")
    void shoudThrow() throws Exception {
        TpsPersonData personData = JAXB.unmarshal(fetchTestXml(), TpsPersonData.class);
        Set<String> idents = extractIdents(personData);
        mockMqError();
        mockBackupMq(personData);

        Set<TpsStatus> identerFinnes = identTpsService.checkIdentsInTps(idents, new ArrayList<>());

        assertResponseOk(personData, identerFinnes);
    }

    @Test
    @DisplayName("Sjekker at mapping av om identer finnes i TPS går ok")
    void finnesITps() throws Exception {
        TpsPersonData personData = JAXB.unmarshal(fetchTestXml(), TpsPersonData.class);
        Set<String> idents = extractIdents(personData);

        mockMqSuccess(personData);

        Set<TpsStatus> identerFinnes = identTpsService.checkIdentsInTps(idents, new ArrayList<>());

        assertResponseOk(personData, identerFinnes);
    }

    @Test
    @DisplayName("Sjekker at mapping av om identer finnes i TPS går ok")
    void finnesITpsNew() throws Exception {
        TpsPersonData personData = JAXB.unmarshal(fetchTestXml(), TpsPersonData.class);
        Set<String> idents = extractIdents(personData);

        mockMqSuccess(personData);

        Set<TpsStatus> identerFinnes = identTpsService.checkIdentsInTps(idents, new ArrayList<>());

        assertResponseOk(personData, identerFinnes);
    }

    @Test
    @DisplayName("Skal returnere når tom liste med fnr")
    void emptyFnrs() {
        Set<TpsStatus> identerFinnes = identTpsService.checkIdentsInTps(new HashSet<>(), new ArrayList<>());

        assertEquals(0, identerFinnes.size());
    }

    @Test
    @DisplayName("Skal feile ved sending av melding til kø")
    void shouldThrow() throws Exception {
        mockMessageError();

        Set<String> idents = extractIdents(JAXB.unmarshal(fetchTestXml(), TpsPersonData.class));

        assertThrows(RuntimeException.class, () -> identTpsService.checkIdentsInTps(idents, new ArrayList<>()));
    }

    private void assertResponseOk(TpsPersonData personData, Set<TpsStatus> identerFinnes) {
        List<PersondataFraTpsM201.AFnr.EFnr> eFnrs = personData.getTpsSvar().getPersonDataM201().getAFnr().getEFnr();
        eFnrs.forEach(eFnr -> {
            boolean finnes = eFnr.getSvarStatus() == null || !"08".equals(eFnr.getSvarStatus().getReturStatus());
            assertTrue(identerFinnes.contains(new TpsStatus(eFnr.getFnr(), finnes)));
        });
    }

    private Set<String> extractIdents(TpsPersonData personData) {
        return personData.getTpsSvar().getPersonDataM201().getAFnr().getEFnr().stream()
                .map(PersondataFraTpsS201::getFnr)
                .collect(Collectors.toSet());
    }

    private void mockQueueContext() {
        ReflectionTestUtils.setField(queueContext, "successEnvs", Arrays.asList("t1", "q6"));
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

    private void mockMessageError() throws JMSException {
        when(messageQueue.sendMessage(anyString())).thenThrow(new JMSException("JMS error"));
        when(messageQueueFactory.createMessageQueue(anyString())).thenReturn(messageQueue);
    }

    private static InputStream fetchTestXml() throws IOException {
        return (new ClassPathResource("mq/persondataSuccess.xml")).getInputStream();
    }
}
