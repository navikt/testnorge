package no.nav.registre.frikort.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.frikort.provider.rs.response.LeggPaaKoeStatus;
import no.nav.registre.frikort.provider.rs.response.SyntetiserFrikortResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class MqService {

    private final JmsTemplate jmsTemplate;

    @Value("${mq.q2.queue.name}")
    private String koeNavn;

    public void leggTilMeldingerPaaKoe(List<SyntetiserFrikortResponse> syntetiskeFrikort) {
        for (var frikort : syntetiskeFrikort) {
            try {
                leggTilMeldingPaaKoe(frikort.getXml());
                frikort.setLagtPaaKoe(LeggPaaKoeStatus.OK);
            } catch (RuntimeException e) {
                frikort.setLagtPaaKoe(LeggPaaKoeStatus.ERROR);
                log.error("Kunne ikke legge melding på kø", e);
            }
        }
    }

    private void leggTilMeldingPaaKoe(String xmlMelding) throws RuntimeException {
        jmsTemplate.send(koeNavn, session -> session.createTextMessage(xmlMelding));
    }
}