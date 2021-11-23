package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.TelefonnummerRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TelefonnummerResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonnummerDTO;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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
public class TelefonnummerService {

    private final MapperFacade mapperFacade;
    private final EndringsmeldingConsumer endringsmeldingConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;

    public TelefonnummerService(MapperFacade mapperFacade, EndringsmeldingConsumer endringsmeldingConsumer) throws JAXBException {
        this.mapperFacade = mapperFacade;
        this.endringsmeldingConsumer = endringsmeldingConsumer;

        this.requestContext = JAXBContext.newInstance(TelefonnummerRequest.class);
        this.responseContext = JAXBContext.newInstance(TelefonnummerResponse.class);
    }

    private static TelefonnummerRequest updateRequest(TelefonnummerRequest request, boolean isEndre) {

        if (isEndre) {
            request.getSfeAjourforing().setOpphorTelefon(null);

        } else {
            request.getSfeAjourforing().setEndreTelefon(null);
        }

        return request;
    }

    public Map<String, TpsMeldingResponse> endreTelefonnummer(String ident, TelefonnummerDTO telefonnummer, List<String> miljoer) {

        return endreTelefonnummer(true, ident, telefonnummer, miljoer);
    }

    public Map<String, TpsMeldingResponse> opphoerTelefonnummer(String ident, TelefonnummerDTO telefonnummer, List<String> miljoer) {

        return endreTelefonnummer(false, ident, telefonnummer, miljoer);
    }

    private Map<String, TpsMeldingResponse> endreTelefonnummer(boolean isEndre, String ident, TelefonnummerDTO telefonnummer, List<String> miljoer) {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", ident);

        var request = mapperFacade.map(telefonnummer, TelefonnummerRequest.class, context);

        var requestXml = marshallToXML(requestContext, updateRequest(request, isEndre));
        var miljoerResponse = endringsmeldingConsumer.sendMessage(requestXml, miljoer);

        return miljoerResponse.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> {

                    try {
                        return getResponseStatus(TpsMeldingCommand.NO_RESPONSE.equals(entry.getValue()) ? null :
                                (TelefonnummerResponse) unmarshallFromXml(responseContext, entry.getValue()));

                    } catch (JAXBException e) {
                        log.error(e.getMessage(), e);
                        return getErrorStatus(e);
                    }
                }));
    }
}
