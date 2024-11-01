package no.nav.testnav.oppdragservice.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragResponse;
import no.nav.testnav.oppdragservice.consumer.OppdragConsumer;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
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

    public OppdragResponse sendInnOppdrag(OppdragRequest oppdragRequest) {

        var request  = mapperFacade.map(oppdragRequest, SendInnOppdragRequest.class);

        var oppdragResponse = oppdragConsumer.sendOppdrag(request);

        return mapperFacade.map(oppdragResponse, OppdragResponse.class);
    }

    @SneakyThrows
    private String marshallToXml(SendInnOppdragRequest melding) {

        var marshaller = jaxbContext.createMarshaller();
        var writer = new StringWriter();
        marshaller.marshal(melding, writer);

        return writer.toString();
    }
}