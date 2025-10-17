package no.nav.testnav.apps.tpsmessagingservice.consumer;

import no.nav.testnav.apps.tpsmessagingservice.factory.ConnectionFactoryFactory;
import org.springframework.stereotype.Service;

@Service
public class XmlMeldingConsumer extends TpsConsumer {

    public XmlMeldingConsumer(ConnectionFactoryFactory connectionFactoryFactory) {
        super(connectionFactoryFactory);
    }

    @Override
    protected String getQueueName(String queue, String miljoe) {
        return "";
    }

    @Override
    protected String getErrorMessage(Exception e) {
        return "";
    }
}
