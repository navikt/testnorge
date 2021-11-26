package no.nav.testnav.apps.tpsmessagingservice.consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;
import no.nav.testnav.apps.tpsmessagingservice.factory.ConnectionFactoryFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public abstract class TpsConsumer {

    protected static final String PREFIX_MQ_QUEUES = "QA.";
    private static final String CHANNEL_SUFFIX = "_TESTNAV_TPS_MSG_S";
    private final ConnectionFactoryFactory connectionFactoryFactory;

    @Value("${config.mq.preprod.queueManager}")
    private String queueManagerPreprod;
    @Value("${config.mq.preprod.host}")
    private String hostPreprod;
    @Value("${config.mq.preprod.port}")
    private Integer portPreprod;
    @Value("${config.mq.preprod.user}")
    private String usernamePreprod;
    @Value("${config.mq.preprod.password}")
    private String passwordPreprod;
    @Value("${config.mq.test.queueManager}")
    private String queueManagerTest;
    @Value("${config.mq.test.host}")
    private String hostTest;
    @Value("${config.mq.test.port}")
    private Integer portTest;
    @Value("${config.mq.test.user}")
    private String usernameTest;
    @Value("${config.mq.test.password}")
    private String passwordTest;
    @Value("${config.mq.channel:}")
    private String channel;
    @Value("${config.mq.queue:}")
    private String queue;

    protected TpsConsumer(ConnectionFactoryFactory connectionFactoryFactory) {

        this.connectionFactoryFactory = connectionFactoryFactory;
    }

    private static String getChannelName(String channel, String miljoe) {

        return isBlank(channel) ?
                // 20 er maksimum IBM MQ kanallengde
                (miljoe.toUpperCase() + CHANNEL_SUFFIX).substring(0, 20) :
                channel;
    }

    private static boolean isPreprod(String miljoe) {

        return "Q".equalsIgnoreCase(miljoe.substring(0, 1));
    }

    protected abstract String getQueueName(String queue, String miljoe);

    protected abstract String getErrorMessage(Exception e) throws JAXBException;

    public Map<String, String> sendMessage(String melding, List<String> miljoer) {

        return miljoer.parallelStream()
                .map(miljoe -> {
                    try {
                        return TpsMiljoeResultat.builder()
                                .miljoe(miljoe)
                                .resultat(new TpsMeldingCommand(
                                        connectionFactoryFactory.createConnectionFactory(getQueueManager(miljoe)),
                                        getQueueName(queue, miljoe),
                                        isPreprod(miljoe) ? usernamePreprod : usernameTest,
                                        isPreprod(miljoe) ? passwordPreprod : passwordTest,
                                        melding).call())
                                .build();

                    } catch (Exception e) {
                        try {

                            log.error(e.getMessage(), e);

                            return TpsMiljoeResultat.builder()
                                    .miljoe(miljoe)
                                    .resultat(getErrorMessage(e))
                                    .build();

                        } catch (JAXBException ex) {

                            log.error("Marshalling av feilmelding feilet", ex);
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(toMap(TpsMiljoeResultat::getMiljoe, TpsMiljoeResultat::getResultat));
    }

    private QueueManager getQueueManager(String miljoe) {

        return isPreprod(miljoe) ?
                new QueueManager(queueManagerPreprod, hostPreprod, portPreprod, getChannelName(channel, miljoe)) :
                new QueueManager(queueManagerTest, hostTest, portTest, getChannelName(channel, miljoe));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TpsMiljoeResultat {

        private String miljoe;
        private String resultat;
    }
}
