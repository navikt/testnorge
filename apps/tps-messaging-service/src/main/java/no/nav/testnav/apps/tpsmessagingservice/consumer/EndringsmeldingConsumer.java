package no.nav.testnav.apps.tpsmessagingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.EndringsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingErrorResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;
import no.nav.testnav.apps.tpsmessagingservice.dto.SfeTilbakemelding;
import no.nav.testnav.apps.tpsmessagingservice.factory.ConnectionFactoryFactory;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.EndringsmeldingResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class EndringsmeldingConsumer {

    public static final String REQUEST_QUEUE_ENDRINGSMELDING_ALIAS = "SFE_ENDRINGSMELDING";

    public static final String CHANNEL_SUFFIX = "_TESTNAV_TPS_MSG_S";

    protected static final String PREFIX_MQ_QUEUES = "QA.";
    protected static final String MID_PREFIX_QUEUE_ENDRING = "_412.";

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

    public EndringsmeldingConsumer(ConnectionFactoryFactory connectionFactoryFactory) throws JAXBException {

        this.connectionFactoryFactory = connectionFactoryFactory;
        this.responseErrorContext = JAXBContext.newInstance(EndringsmeldingErrorResponse.class);
    }

    private static String getQueueName(String queue, String miljoe) {

        return isBlank(queue) ?
                String.format("%s%s%s%s", PREFIX_MQ_QUEUES, miljoe.toUpperCase(),
                        MID_PREFIX_QUEUE_ENDRING, REQUEST_QUEUE_ENDRINGSMELDING_ALIAS) :
                queue;
    }

    private static String getChannelName(String channel, String miljoe) {

        return isBlank(channel) ?
                miljoe.toUpperCase() + CHANNEL_SUFFIX :
                channel;
    }

    private String marshallToXML(EndringsmeldingErrorResponse errorResponse) throws JAXBException {

        var marshaller = responseErrorContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        var writer = new StringWriter();
        marshaller.marshal(errorResponse, writer);

        return writer.toString();
    }

    public Map<String, String> sendEndringsmelding(String melding, List<String> miljoer) {

        var resultat = new HashMap<String, String>();

        miljoer.forEach(miljoe -> {
            try {
                resultat.put(miljoe, new EndringsMeldingCommand(
                        connectionFactoryFactory.createConnectionFactory(
                                new QueueManager(queueManager, host, port, getChannelName(channel, miljoe))),
                        getQueueName(queue, miljoe),
                        username,
                        password,
                        melding).call());

            } catch (JMSException e) {
                try {
                    resultat.put(miljoe, marshallToXML(EndringsmeldingErrorResponse.builder()
                            .sfeTilbakemelding(SfeTilbakemelding.builder()
                                    .svarStatus(EndringsmeldingResponseDTO.builder()
                                            .returStatus("08")
                                            .returMelding("Teknisk feil, se logg!")
                                            .utfyllendeMelding(e.getMessage())
                                            .build())
                                    .build())
                            .build()));

                } catch (JAXBException ex) {

                    log.error("Marshalling av feilmelding feilet", ex);
                }

                log.error(e.getMessage(), e);
            }
        });

        return resultat;
    }
}
