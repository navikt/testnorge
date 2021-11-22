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

import javax.jms.JMSException;
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

    @Value("${config.mq.queueManager}")
    private String queueManager;
    @Value("${config.mq.conn-name}")
    private String host;
    @Value("${config.mq.port}")
    private Integer port;
    @Value("${config.mq.user}")
    private String username;
    @Value("${config.mq.password}")
    private String password;
    @Value("${config.mq.channel:}")
    private String channel;
    @Value("${config.mq.queue:}")
    private String queue;

    protected TpsConsumer(ConnectionFactoryFactory connectionFactoryFactory) {

        this.connectionFactoryFactory = connectionFactoryFactory;
    }

    private static String getChannelName(String channel, String miljoe) {

        return isBlank(channel) ?
                miljoe.toUpperCase() + CHANNEL_SUFFIX :
                channel;
    }

    protected abstract String getQueueName(String queue, String miljoe);

    protected abstract String getErrorMessage(JMSException e) throws JAXBException;

    public Map<String, String> sendMessage(String melding, List<String> miljoer) {

        return miljoer.parallelStream()
                .map(miljoe -> {
                    try {
                        return TpsMiljoeResultat.builder()
                                .miljoe(miljoe)
                                .resultat(new TpsMeldingCommand(
                                        connectionFactoryFactory.createConnectionFactory(
                                                new QueueManager(queueManager, host, port, getChannelName(channel, miljoe))),
                                        getQueueName(queue, miljoe),
                                        username,
                                        password,
                                        melding).call())
                                .build();

                    } catch (JMSException e) {
                        try {
                            return TpsMiljoeResultat.builder()
                                    .miljoe(miljoe)
                                    .resultat(getErrorMessage(e))
                                    .build();

                        } catch (JAXBException ex) {

                            log.error("Marshalling av feilmelding feilet", ex);
                        }

                        log.error(e.getMessage(), e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(toMap(TpsMiljoeResultat::getMiljoe, TpsMiljoeResultat::getResultat));
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
