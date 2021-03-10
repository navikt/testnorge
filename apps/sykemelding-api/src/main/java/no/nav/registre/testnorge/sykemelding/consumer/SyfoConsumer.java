package no.nav.registre.testnorge.sykemelding.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;

@Slf4j
@Component
public class SyfoConsumer {
    private final JmsTemplate jmsTemplate;
    private final String queueName;

    public SyfoConsumer(JmsTemplate jmsTemplate, @Value("${syfo.queue.name}") String queueName) {
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
    }

    public void send(Sykemelding sykemelding) {
        String xml = sykemelding.toXml();
        log.info("Legger sykemelding på kø med MsgId {}", sykemelding.getMsgId());
        jmsTemplate.send(queueName, session -> session.createTextMessage(xml));
        log.trace(xml);
    }
}