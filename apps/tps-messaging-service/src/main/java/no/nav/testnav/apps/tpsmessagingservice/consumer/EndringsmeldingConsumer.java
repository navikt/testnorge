package no.nav.testnav.apps.tpsmessagingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.dto.SfePersonDataErrorResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.SfeTilbakeMelding;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.apps.tpsmessagingservice.factory.ConnectionFactoryFactory;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringWriter;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class EndringsmeldingConsumer extends TpsConsumer {

    public static final String XML_REQUEST_QUEUE_ENDRINGSMELDING_ALIAS = "412.SFE_ENDRINGSMELDING";
    private final JAXBContext responseErrorContext;

    public EndringsmeldingConsumer(ConnectionFactoryFactory connectionFactoryFactory) throws JAXBException {
        super(connectionFactoryFactory);
        this.responseErrorContext = JAXBContext.newInstance(SfePersonDataErrorResponse.class);
    }

    @Override
    protected String getQueueName(String queue, String miljoe) {

        return isBlank(queue) ?
                String.format("%s%s_%s", PREFIX_MQ_QUEUES, miljoe.toUpperCase(),
                        XML_REQUEST_QUEUE_ENDRINGSMELDING_ALIAS) :
                queue;
    }

    @Override
    protected String getErrorMessage(JMSException e) throws JAXBException {

        return marshallToXML(SfePersonDataErrorResponse.builder()
                .sfeTilbakeMelding(SfeTilbakeMelding.builder()
                        .svarStatus(TpsMeldingResponse.builder()
                                .returStatus("FEIL")
                                .returMelding("Teknisk feil, se logg!")
                                .utfyllendeMelding(e.getMessage())
                                .build())
                        .build())
                .build());
    }

    private String marshallToXML(SfePersonDataErrorResponse errorResponse) throws JAXBException {

        var marshaller = responseErrorContext.createMarshaller();

        var writer = new StringWriter();
        marshaller.marshal(errorResponse, writer);

        return writer.toString();
    }
}
