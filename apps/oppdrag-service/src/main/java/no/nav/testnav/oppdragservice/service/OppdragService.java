package no.nav.testnav.oppdragservice.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragResponse;
import no.nav.testnav.oppdragservice.consumer.OppdragWSConsumer;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragResponse;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.io.StringWriter;

@Service
public class OppdragService {

    private final JAXBContext jaxbRequestContext;
    private final JAXBContext jaxbResponseContext;
    private final OppdragWSConsumer oppdragWSConsumer;
    private final MapperFacade mapperFacade;

    public OppdragService(OppdragWSConsumer oppdragWSConsumer, MapperFacade mapperFacade) throws JAXBException {
        this.oppdragWSConsumer = oppdragWSConsumer;
        this.mapperFacade = mapperFacade;
        this.jaxbRequestContext = JAXBContext.newInstance(SendInnOppdragRequest.class);
        this.jaxbResponseContext = JAXBContext.newInstance(SendInnOppdragResponse.class);
    }

    public OppdragResponse sendInnOppdrag(String miljoe, OppdragRequest oppdragRequest) {

        var request  = mapperFacade.map(oppdragRequest, SendInnOppdragRequest.class);
        var xmlRequest = marshall(request);

        var oppdragResponse = oppdragWSConsumer.sendOppdrag(miljoe, request);
//                .map(this::unmarshall)
//                .map(oppdragResponse -> mapperFacade.map(oppdragResponse, OppdragResponse.class));

        var response = mapperFacade.map(oppdragResponse, OppdragResponse.class);

        return response;
    }

    @SneakyThrows
    private String marshall(SendInnOppdragRequest melding) {

        var marshaller = jaxbRequestContext.createMarshaller();
        var writer = new StringWriter();
        marshaller.marshal(melding, writer);

        return writer.toString();
    }

    @SneakyThrows
    private SendInnOppdragResponse unmarshall(String melding) {

        var unmarshaller = jaxbResponseContext.createUnmarshaller();
        var reader = new StringReader(melding);

        return (SendInnOppdragResponse) unmarshaller.unmarshal(reader);
    }
}