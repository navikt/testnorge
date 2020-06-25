package no.nav.registre.frikort.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class MqService {

    private final JmsTemplate jmsTemplate;

    @Value("${mq.q2.queue.name}")
    private String koeNavn;

    public void leggTilMeldingerPaaKoe(List<String> xmlMeldinger) {
        for (var melding : xmlMeldinger) {
            leggTilMeldingPaaKoe(melding);
        }
    }

    public void leggTilMeldingPaaKoe(String xmlMelding) {
        try {
            jmsTemplate.send(koeNavn, session -> session.createTextMessage(xmlMelding));
        } catch (Exception e) {
            log.error("Kunne ikke legge melding på kø.", e);
            throw e;
        }
    }

}