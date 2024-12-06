package no.nav.registre.testnorge.sykemelding.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SyfoConsumer {

    private final JmsTemplate jmsTemplate;
    private final String queueName;

    public SyfoConsumer(JmsTemplate jmsTemplate, @Value("${syfo.queue.name}") String queueName) {
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
    }

    public SykemeldingResponseDTO send(Sykemelding sykemelding) {

        var xml = sykemelding.toXml();
        log.info("Legger sykemelding på kø med MsgId {}\n{}", sykemelding.getMsgId(), sykemelding);
        jmsTemplate.send(queueName, session -> session.createTextMessage(xml));
        log.trace(xml);

        return SykemeldingResponseDTO.builder()
                .sykemeldingId(sykemelding.getMsgId())
                .status(HttpStatus.OK)
                .build();
    }
}