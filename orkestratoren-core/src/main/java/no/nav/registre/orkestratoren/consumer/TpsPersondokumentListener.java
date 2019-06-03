package no.nav.registre.orkestratoren.consumer;

import static java.lang.String.format;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;
import no.nav.registre.orkestratoren.consumer.utils.PersondokumentConverter;

import no.rtv.namespacetps.TpsPersonDokumentType;

@Slf4j
@Component
public class TpsPersondokumentListener {

    @Autowired
    private PersondokumentConverter persondokumentConverter;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @JmsListener(destination = "${queue.queueName}")
    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "TPS" })
    public void lesFraKoe(Message message) throws JMSException {
        try {
            String persondokumentAsXml = ((TextMessage) message).getText();
            TpsPersonDokumentType tpsPersondokument = persondokumentConverter.convert(persondokumentAsXml);

            if (tpsPersondokument.getPerson().getPersonIdent().isEmpty()) {
                log.warn("Persondokument fra tps inneholder ingen identer");
            } else {
                hodejegerenConsumer.sendTpsPersondokumentTilHodejegeren(tpsPersondokument);
            }
        } catch (RuntimeException e) {
            log.error(format("Failed to process JMS message: [Id: %s].", message.getJMSCorrelationID()), e);
        }
    }
}
