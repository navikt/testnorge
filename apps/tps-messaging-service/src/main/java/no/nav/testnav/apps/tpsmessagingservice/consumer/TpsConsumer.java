package no.nav.testnav.apps.tpsmessagingservice.consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;
import no.nav.testnav.apps.tpsmessagingservice.dto.SfeTilbakeMelding;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingErrorResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.apps.tpsmessagingservice.factory.ConnectionFactoryFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public abstract class TpsConsumer {

    protected static final String PREFIX_MQ_QUEUES = "QA.";
    private static final String CHANNEL_SUFFIX = "_TESTNAV_TPS_MSG_S";
    private final ConnectionFactoryFactory connectionFactoryFactory;
    private final JAXBContext responseErrorContext;
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

    public TpsConsumer(ConnectionFactoryFactory connectionFactoryFactory) throws JAXBException {

        this.connectionFactoryFactory = connectionFactoryFactory;
        this.responseErrorContext = JAXBContext.newInstance(TpsMeldingErrorResponse.class);
    }

    private static String getChannelName(String channel, String miljoe) {

        return isBlank(channel) ?
                miljoe.toUpperCase() + CHANNEL_SUFFIX :
                channel;
    }

    protected abstract String getQueueName(String queue, String miljoe);

    private String marshallToXML(TpsMeldingErrorResponse errorResponse) throws JAXBException {

        var marshaller = responseErrorContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        var writer = new StringWriter();
        marshaller.marshal(errorResponse, writer);

        return writer.toString();
    }

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
                                    .resultat(marshallToXML(TpsMeldingErrorResponse.builder()
                                            .sfeTilbakeMelding(SfeTilbakeMelding.builder()
                                                    .svarStatus(TpsMeldingResponse.builder()
                                                            .returStatus("FEIL")
                                                            .returMelding("Teknisk feil, se logg!")
                                                            .utfyllendeMelding(e.getMessage())
                                                            .build())
                                                    .build())
                                            .build()))
                                    .build();

                        } catch (JAXBException ex) {

                            log.error("Marshalling av feilmelding feilet", ex);
                        }

                        log.error(e.getMessage(), e);
                    }
                    return null;
                })
                .filter(entry -> nonNull(entry.getResultat()))
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
