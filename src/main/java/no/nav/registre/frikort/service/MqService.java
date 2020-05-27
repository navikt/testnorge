package no.nav.registre.frikort.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.jms.core.JmsTemplate;

@Slf4j
@Service
public class MqService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${mq.q2.queue.name}")
    private String koeNavn;

    public void leggTilMeldingPaaKoe(String xmlMelding) {
        try {
            jmsTemplate.send(koeNavn, session -> session.createTextMessage(xmlMelding));
        } catch (Exception e) {
            log.error("Kunne ikke legge melding på kø.", e);
            throw e;
        }
    }

}