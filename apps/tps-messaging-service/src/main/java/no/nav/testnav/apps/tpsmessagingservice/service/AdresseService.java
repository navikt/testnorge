package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.PostadresseRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.PostadresseResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseUtlandDTO;
import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getErrorStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getResponseStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.marshallToXML;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.unmarshallFromXml;

@Slf4j
@Service
public class AdresseService {

    private final MapperFacade mapperFacade;
    private final EndringsmeldingConsumer endringsmeldingConsumer;
    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;

    public AdresseService(MapperFacade mapperFacade, EndringsmeldingConsumer endringsmeldingConsumer,
                          TestmiljoerServiceConsumer testmiljoerServiceConsumer) throws JAXBException {

        this.mapperFacade = mapperFacade;
        this.endringsmeldingConsumer = endringsmeldingConsumer;
        this.testmiljoerServiceConsumer = testmiljoerServiceConsumer;
        this.requestContext = JAXBContext.newInstance(PostadresseRequest.class);
        this.responseContext = JAXBContext.newInstance(PostadresseResponse.class);
    }

    public Map<String, TpsMeldingResponse> endreAdresseUtland(String ident, AdresseUtlandDTO postadresseUtland, List<String> miljoer) {

        miljoer = isNull(miljoer) ? testmiljoerServiceConsumer.getMiljoer() : miljoer;

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", ident);

        var request = mapperFacade.map(postadresseUtland, PostadresseRequest.class, context);
        var requestXml = marshallToXML(requestContext, request);
        var miljoerResponse = endringsmeldingConsumer.sendMessage(requestXml, miljoer);

        return miljoerResponse.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {

                    try {
                        return getResponseStatus(TpsMeldingCommand.NO_RESPONSE.equals(entry.getValue()) ? null :
                                (PostadresseResponse) unmarshallFromXml(responseContext, entry.getValue()));

                    } catch (JAXBException e) {
                        log.error(e.getMessage(), e);
                        return getErrorStatus(e);
                    }
                }));
    }
}
