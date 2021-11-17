package no.nav.testnav.apps.tpsmessagingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.factory.ConnectionFactoryFactory;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class ServicerutineConsumer extends TpsConsumer {

    private static final String XML_REQUEST_QUEUE_SERVICE_RUTINE_ALIAS = "411.TPS_FORESPORSEL_XML_O";

    public ServicerutineConsumer(ConnectionFactoryFactory connectionFactoryFactory) throws JAXBException {
        super(connectionFactoryFactory);
    }

    @Override
    protected String getQueueName(String queue, String miljoe) {

        return isBlank(queue) ?
                String.format("%s%s_%s", PREFIX_MQ_QUEUES, miljoe.toUpperCase(),
                        XML_REQUEST_QUEUE_SERVICE_RUTINE_ALIAS) :
                queue;
    }
}
