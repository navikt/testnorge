package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.OpphoerEgenansattRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.OpphoerEgenansattResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.OpprettEgenansattRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.OpprettEgenansattResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.EndringsmeldingResponseDTO;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getErrorStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getOkeyStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.marshallToXML;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.unmarshallFromXml;

@Slf4j
@Service
public class OpphoerEgenansattService {

    private final MapperFacade mapperFacade;
    private final EndringsmeldingConsumer endringsmeldingConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;

    public OpphoerEgenansattService(MapperFacade mapperFacade, EndringsmeldingConsumer endringsmeldingConsumer) throws JAXBException {
        this.mapperFacade = mapperFacade;
        this.endringsmeldingConsumer = endringsmeldingConsumer;

        this.requestContext = JAXBContext.newInstance(OpphoerEgenansattRequest.class);
        this.responseContext = JAXBContext.newInstance(OpprettEgenansattResponse.class);
    }

    public Map<String, EndringsmeldingResponseDTO> opphoerEgenansatt(String ident, List<String> miljoer) {
        try {
            var context = new MappingContext.Factory().getContext();
            context.setProperty("ident", ident);

            var request = mapperFacade.map(new EndringsmeldingRequest(), OpphoerEgenansattRequest.class, context);
            var requestXml = marshallToXML(requestContext, request);
            var miljoerResponse = endringsmeldingConsumer.sendEndringsmelding(requestXml, miljoer);

            return miljoerResponse.entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey(), entry -> {

                        try {
                            var response = (OpphoerEgenansattResponse) unmarshallFromXml(responseContext, entry.getValue());
                            return getOkeyStatus(response.getSfeTilbakemelding().getSvarStatus());

                        } catch (JAXBException e) {
                            log.error(e.getMessage(), e);
                            return getErrorStatus(e);
                        }
                    }));

        } catch (JAXBException e) {

            log.error(e.getMessage(), e);
            return Map.of("NA", getErrorStatus(e));
        }
    }
}
