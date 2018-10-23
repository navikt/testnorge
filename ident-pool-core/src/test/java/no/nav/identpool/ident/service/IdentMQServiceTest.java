package no.nav.identpool.ident.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.xml.bind.JAXB;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.identpool.ident.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ident.ajourhold.mq.factory.MessageQueueFactory;
import no.nav.identpool.ident.ajourhold.tps.generator.IdentGenerator;
import no.nav.identpool.ident.rest.v1.HentIdenterRequest;
import no.nav.tps.ctg.m201.domain.PersondataFraTpsM201;
import no.nav.tps.ctg.m201.domain.StatusFraTPSType;
import no.nav.tps.ctg.m201.domain.TpsPersonData;
import no.nav.tps.ctg.m201.domain.TpsSvarType;

@RunWith(MockitoJUnitRunner.class)
public class IdentMQServiceTest {

    private static SecureRandom random = new SecureRandom();

    public static String getSvarStatus() {
        switch (random.nextInt(4)) {
            case 0:
                return "00";
            case 1:
                return "04";
            case 2:
                return "08";
        }
        return null;
    }

    @Test
    public void finnesITps() throws JMSException {
        List<String> identer = IdentGenerator.genererIdenter(HentIdenterRequest.builder()
                .antall(100).foedtEtter(LocalDate.now()).build());

        TpsPersonData personData = new TpsPersonData();
        TpsSvarType tpsSvar = new TpsSvarType();
        PersondataFraTpsM201 fraTpsM201 = new PersondataFraTpsM201();
        PersondataFraTpsM201.AFnr aFnr = new PersondataFraTpsM201.AFnr();

        List<PersondataFraTpsM201.AFnr.EFnr> eFnrs = identer.stream().map(fnr -> {
            PersondataFraTpsM201.AFnr.EFnr eFnr = new PersondataFraTpsM201.AFnr.EFnr();
            eFnr.setFnr(fnr);
            StatusFraTPSType status = new StatusFraTPSType();
            eFnr.setSvarStatus(status);
            status.setReturStatus(getSvarStatus());
            return eFnr;
        }).collect(Collectors.toList());

        aFnr.getEFnr().addAll(eFnrs);
        fraTpsM201.setAFnr(aFnr);
        tpsSvar.setPersonDataM201(fraTpsM201);
        personData.setTpsSvar(tpsSvar);

        StringWriter stringWriter = new StringWriter();
        JAXB.marshal(personData, stringWriter);

        MessageQueueFactory messageQueueFactory = mock(MessageQueueFactory.class);
        MessageQueue messageQueue = mock(MessageQueue.class);
        when(messageQueue.sendMessage(anyString())).thenReturn(stringWriter.toString());
        when(messageQueueFactory.createMessageQueue(anyString())).thenReturn(messageQueue);

        IdentMQService identMQService = new IdentMQService(messageQueueFactory);
        Map<String, Boolean> identerFinnes = identMQService.finnesITps(Arrays.asList("t1", "t4"), identer);
        eFnrs.forEach(eFnr -> {
            Boolean finnes = eFnr.getSvarStatus() != null && !"08".equals(eFnr.getSvarStatus().getReturStatus());
            assertEquals(identerFinnes.get(eFnr.getFnr()), finnes);
        });
    }
}
