package no.nav.testnav.oppdragservice.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import no.nav.testnav.oppdragservice.consumer.OppdragConsumer;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragResponse;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class OppdragService {

    private final JAXBContext jaxbContext;
    private final OppdragConsumer oppdragConsumer;
    private final MapperFacade mapperFacade;

    public OppdragService(OppdragConsumer oppdragConsumer, MapperFacade mapperFacade) throws JAXBException {
        this.oppdragConsumer = oppdragConsumer;
        this.mapperFacade = mapperFacade;
        this.jaxbContext = JAXBContext.newInstance(SendInnOppdragRequest.class);
    }

    public SendInnOppdragResponse sendInnOppdrag(OppdragRequest oppdragRequest) {

        var request  = mapperFacade.map(oppdragRequest, SendInnOppdragRequest.class);
        var xmlRequest = marshallToXml(request);

        oppdragConsumer.sendOppdrag(xmlRequest);
        return null;
    }

    @SneakyThrows
    private String marshallToXml(SendInnOppdragRequest melding) {

        var marshaller = jaxbContext.createMarshaller();
        var writer = new StringWriter();
        marshaller.marshal(melding, writer);

        return writer.toString();
    }
}