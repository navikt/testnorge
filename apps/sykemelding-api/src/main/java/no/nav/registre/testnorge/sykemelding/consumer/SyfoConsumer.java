package no.nav.registre.testnorge.sykemelding.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

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
        log.info("Legger sykemelding på kø med MsgId {}\n{}", sykemelding.getMsgId(), sykemelding);
        jmsTemplate.send(queueName, session -> session.createTextMessage(xml));
        log.trace(xml);
    }
}