package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.OpprettEgenansattRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.OpprettEgenansattResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.EndringsmeldingResponseDTO;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
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
public class OpprettEgenansattService {

    private final MapperFacade mapperFacade;
    private final EndringsmeldingConsumer endringsmeldingConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;

    public OpprettEgenansattService(MapperFacade mapperFacade, EndringsmeldingConsumer endringsmeldingConsumer) throws JAXBException {
        this.mapperFacade = mapperFacade;
        this.endringsmeldingConsumer = endringsmeldingConsumer;

        this.requestContext = JAXBContext.newInstance(OpprettEgenansattRequest.class);
        this.responseContext = JAXBContext.newInstance(OpprettEgenansattResponse.class);
    }

    public Map<String, EndringsmeldingResponseDTO> opprettEgenansatt(String ident, LocalDate fraOgMed, List<String> miljoer) {
        try {
            var context = new MappingContext.Factory().getContext();
            context.setProperty("ident", ident);
            context.setProperty("fraOgMed", fraOgMed);

            var request = mapperFacade.map(new EndringsmeldingRequest(), OpprettEgenansattRequest.class, context);
            var requestXml = marshallToXML(requestContext, request);
            var miljoerResponse = endringsmeldingConsumer.sendEndringsmelding(requestXml, miljoer);

            return miljoerResponse.entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey(), entry -> {

                        try {
                            var response = (OpprettEgenansattResponse) unmarshallFromXml(responseContext, entry.getValue());
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
