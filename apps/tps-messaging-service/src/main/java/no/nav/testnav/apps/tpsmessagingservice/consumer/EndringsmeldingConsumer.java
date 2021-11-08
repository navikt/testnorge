package no.nav.testnav.apps.tpsmessagingservice.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.EndringsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;
import no.nav.testnav.apps.tpsmessagingservice.factory.ConnectionFactoryFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndringsmeldingConsumer {

    public static final String REQUEST_QUEUE_SERVICE_RUTINE_ALIAS = "TPS_FORESPORSEL_XML_O";
    public static final String REQUEST_QUEUE_ENDRINGSMELDING_ALIAS = "SFE_ENDRINGSMELDING";

    public static final String CHANNEL_SUFFIX = "_TESTNAV_TPS_MSG_S";
    public static final String TPSF_KILDE = "TPSF";

    protected static final String DEV_ENVIRONMENT = "D8";
    protected static final String PREFIX_MQ_QUEUES = "QA.";
    protected static final String MID_PREFIX_QUEUE_ENDRING = "_412.";
    protected static final String MID_PREFIX_QUEUE_HENTING = "_411.";
    protected static final String ZONE = "FSS";
    protected static final String FASIT_APP_NAME = "dummy";
    protected static final String QUEUE_MANAGER_ALIAS = "mqGateway";

    private final ConnectionFactoryFactory connectionFactoryFactory;

    @Value("${ibm.mq.queueManager}")
    private String queueManager;

    @Value("${ibm.mq.host}")
    private String host;

    @Value("${ibm.mq.port}")
    private Integer port;

    @Value("${ibm.mq.user}")
    private String username;

    @Value("${ibm.mq.password}")
    private String password;

    public Map<String, String> sendEndringsmelding(String melding, List<String> miljoer) {

        var resultat = new HashMap<String, String>();

        miljoer.forEach(miljoe -> {
            try {
                resultat.put(miljoe,
                        new EndringsMeldingCommand(
                                connectionFactoryFactory.createConnectionFactory(
                                        new QueueManager(queueManager, host, port, miljoe.toUpperCase() + CHANNEL_SUFFIX)),
                                getQueueName(miljoe),
                                username,
                                password,
                                melding).call());
            } catch (JMSException e) {
                resultat.put(miljoe, e.getMessage());
                log.error(e.getMessage(), e);
            }
        });

        return resultat;
    }

    private static String getQueueName(String miljoe) {

        return String.format("%s%s%s%s", PREFIX_MQ_QUEUES, miljoe.toUpperCase(), MID_PREFIX_QUEUE_ENDRING, REQUEST_QUEUE_ENDRINGSMELDING_ALIAS);

    }
}
