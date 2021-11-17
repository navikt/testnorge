package no.nav.testnav.apps.tpsmessagingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.factory.ConnectionFactoryFactory;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class EndringsmeldingConsumer extends TpsConsumer {

    public static final String XML_REQUEST_QUEUE_ENDRINGSMELDING_ALIAS = "412.SFE_ENDRINGSMELDING";

    public EndringsmeldingConsumer(ConnectionFactoryFactory connectionFactoryFactory) throws JAXBException {
        super(connectionFactoryFactory);
    }

    @Override
    protected String getQueueName(String queue, String miljoe) {

        return isBlank(queue) ?
                String.format("%s%s_%s", PREFIX_MQ_QUEUES, miljoe.toUpperCase(),
                        XML_REQUEST_QUEUE_ENDRINGSMELDING_ALIAS) :
                queue;
    }
}
