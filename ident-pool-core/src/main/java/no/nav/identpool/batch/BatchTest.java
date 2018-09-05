package no.nav.identpool.batch;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import no.nav.identpool.batch.consumers.MessageQueueConsumer;
import no.nav.identpool.batch.factory.MessageQueueFactory;

@Component
@RequiredArgsConstructor
public class BatchTest {
    private final MessageQueueFactory messageQueueFactory;

    @PostConstruct
    private void init() throws JMSException {
        for (int i = 0; i < 10; ++i) {
            MessageQueueConsumer messageQueueConsumer = messageQueueFactory.createMessageQueue("t" + i);
            MessageQueueConsumer messageQueueConsumerQ = messageQueueFactory.createMessageQueue("q" + i);
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<tpsPersonData xmlns=\"http://www.rtv.no/NamespaceTPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" schemaLocation=\"http://www.rtv"
                    + ".no/NamespaceTPS\">\n"
                    + "\t<tpsServiceRutine>\n"
                    + "\t\t<serviceRutinenavn>FS03-FDNUMMER-PERSDATA-O</serviceRutinenavn>\n"
                    + "\t\t<fnr>01110110000</fnr>\n"
                    + "\t\t<aksjonsKode>F</aksjonsKode>\n"
                    + "\t\t<aksjonsKode2>0</aksjonsKode2>\n"
                    + "\t</tpsServiceRutine>\n"
                    + "</tpsPersonData>";
            System.out.println(messageQueueConsumer.sendMessage(xml));
            System.out.println(messageQueueConsumerQ.sendMessage(xml));
        }
    }
}
