package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.SikkerhetstiltakRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.SikkerhetstiltakResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SikkerhetTiltakDTO;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getErrorStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getResponseStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.marshallToXML;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.unmarshallFromXml;

@Slf4j
@Service
public class SikkerhetstiltakService {

    private final MapperFacade mapperFacade;
    private final EndringsmeldingConsumer endringsmeldingConsumer;
    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;

    public SikkerhetstiltakService(MapperFacade mapperFacade, EndringsmeldingConsumer endringsmeldingConsumer,
                                   TestmiljoerServiceConsumer testmiljoerServiceConsumer) throws JAXBException {

        this.mapperFacade = mapperFacade;
        this.endringsmeldingConsumer = endringsmeldingConsumer;
        this.testmiljoerServiceConsumer = testmiljoerServiceConsumer;

        this.requestContext = JAXBContext.newInstance(SikkerhetstiltakRequest.class);
        this.responseContext = JAXBContext.newInstance(SikkerhetstiltakResponse.class);
    }

    private static SikkerhetstiltakRequest updateRequest(SikkerhetstiltakRequest request, boolean isOpprett) {

        if (isOpprett) {
            request.getSfeAjourforing().setOpphorSikkerhetsTiltak(null);

        } else {
            request.getSfeAjourforing().setOpprettSikkerhetsTiltak(null);
        }

        return request;
    }

    public Map<String, TpsMeldingResponse> endreSikkerhetstiltak(String ident, SikkerhetTiltakDTO sikkerhetstiltak, List<String> miljoer) {

        return endreSikkerhetstiltak(true, ident, sikkerhetstiltak, miljoer);
    }

    public Map<String, TpsMeldingResponse> opphoerSikkerhetstiltak(String ident, List<String> miljoer) {

        return endreSikkerhetstiltak(false, ident, new SikkerhetTiltakDTO(), miljoer);
    }

    private Map<String, TpsMeldingResponse> endreSikkerhetstiltak(boolean isEndre, String ident, SikkerhetTiltakDTO sikkerhetstiltak, List<String> miljoer) {

        miljoer = isNull(miljoer) ? testmiljoerServiceConsumer.getMiljoer() : miljoer;

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", ident);

        var request = mapperFacade.map(sikkerhetstiltak, SikkerhetstiltakRequest.class, context);

        var requestXml = marshallToXML(requestContext, updateRequest(request, isEndre));
        var miljoerResponse = endringsmeldingConsumer.sendMessage(requestXml, miljoer);

        return miljoerResponse.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> {

                    try {
                        return getResponseStatus(TpsMeldingCommand.NO_RESPONSE.equals(entry.getValue()) ? null :
                                (SikkerhetstiltakResponse) unmarshallFromXml(responseContext, entry.getValue()));

                    } catch (JAXBException e) {
                        log.error(e.getMessage(), e);
                        return getErrorStatus(e);
                    }
                }));
    }
}
