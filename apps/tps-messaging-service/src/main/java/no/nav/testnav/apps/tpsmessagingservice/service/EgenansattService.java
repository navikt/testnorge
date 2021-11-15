package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.dto.EgenansattRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.EgenansattResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.EndringsmeldingResponseDTO;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getErrorStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getResponseStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.marshallToXML;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.unmarshallFromXml;

@Slf4j
@Service
public class EgenansattService {

    private final MapperFacade mapperFacade;
    private final EndringsmeldingConsumer endringsmeldingConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;

    public EgenansattService(MapperFacade mapperFacade, EndringsmeldingConsumer endringsmeldingConsumer) throws JAXBException {
        this.mapperFacade = mapperFacade;
        this.endringsmeldingConsumer = endringsmeldingConsumer;

        this.requestContext = JAXBContext.newInstance(EgenansattRequest.class);
        this.responseContext = JAXBContext.newInstance(EgenansattResponse.class);
    }

    private static EgenansattRequest updateRequest(EgenansattRequest request, boolean isOpprett) {

        if (isOpprett) {
            request.getSfeAjourforing().setOpphorEgenAnsatt(null);

        } else {
            request.getSfeAjourforing().setEndreEgenAnsatt(null);
        }

        return request;
    }

    public Map<String, EndringsmeldingResponseDTO> opprettEgenansatt(String ident, LocalDate fraOgMed, List<String> miljoer) {

        return endreEgenansatt(true, ident, fraOgMed, miljoer);
    }

    public Map<String, EndringsmeldingResponseDTO> opphoerEgenansatt(String ident, List<String> miljoer) {

        return endreEgenansatt(false, ident, null, miljoer);
    }

    private Map<String, EndringsmeldingResponseDTO> endreEgenansatt(boolean isOpprett, String ident, LocalDate fraOgMed, List<String> miljoer) {
        try {
            var context = new MappingContext.Factory().getContext();
            context.setProperty("ident", ident);
            context.setProperty("fraOgMed", fraOgMed);

            var request = mapperFacade.map(new EndringsmeldingRequest(), EgenansattRequest.class, context);

            var requestXml = marshallToXML(requestContext, updateRequest(request, isOpprett));
            var miljoerResponse = endringsmeldingConsumer.sendEndringsmelding(requestXml, miljoer);

            return miljoerResponse.entrySet().stream()
                    .collect(Collectors.toMap(Entry::getKey, entry -> {

                        try {
                            var response = (EgenansattResponse) unmarshallFromXml(responseContext, entry.getValue());
                            return getResponseStatus(response);

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
