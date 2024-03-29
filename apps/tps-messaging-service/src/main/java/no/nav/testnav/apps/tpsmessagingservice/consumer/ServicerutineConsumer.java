package no.nav.testnav.apps.tpsmessagingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsPersonDataErrorResponse;
import no.nav.testnav.apps.tpsmessagingservice.factory.ConnectionFactoryFactory;
import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.StringWriter;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class ServicerutineConsumer extends TpsConsumer {

    private static final String XML_REQUEST_QUEUE_SERVICE_RUTINE_ALIAS = "411.TPS_FORESPORSEL_XML_O";
    private final JAXBContext responseErrorContext;

    public ServicerutineConsumer(ConnectionFactoryFactory connectionFactoryFactory) throws JAXBException {
        super(connectionFactoryFactory);
        this.responseErrorContext = JAXBContext.newInstance(TpsPersonDataErrorResponse.class);
    }

    @Override
    protected String getQueueName(String queue, String miljoe) {

        return isBlank(queue) ?
                "%s%s_%s".formatted(PREFIX_MQ_QUEUES, miljoe.toUpperCase(),
                        XML_REQUEST_QUEUE_SERVICE_RUTINE_ALIAS) :
                queue;
    }

    @Override
    protected String getErrorMessage(Exception e) throws JAXBException {

        return marshallToXML(TpsPersonDataErrorResponse.builder()
                .tpsSvar(TpsPersonDataErrorResponse.TpsSvar.builder()
                        .svarStatus(TpsMeldingResponse.builder()
                                .returStatus("FEIL")
                                .returMelding("Teknisk feil, se logg!")
                                .utfyllendeMelding(e.getMessage())
                                .build())
                        .build())
                .build());
    }

    private String marshallToXML(TpsPersonDataErrorResponse errorResponse) throws JAXBException {

        var marshaller = responseErrorContext.createMarshaller();

        var writer = new StringWriter();
        marshaller.marshal(errorResponse, writer);

        return writer.toString();
    }
}
