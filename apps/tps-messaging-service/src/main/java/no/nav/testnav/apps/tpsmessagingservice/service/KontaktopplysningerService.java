package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getErrorStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getResponseStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.marshallToXML;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.unmarshallFromXml;

@Slf4j
@Service
public class KontaktopplysningerService {

    private final MapperFacade mapperFacade;
    private final EndringsmeldingConsumer endringsmeldingConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;

    public KontaktopplysningerService(MapperFacade mapperFacade, EndringsmeldingConsumer endringsmeldingConsumer) throws JAXBException {
        this.mapperFacade = mapperFacade;
        this.endringsmeldingConsumer = endringsmeldingConsumer;

        this.requestContext = JAXBContext.newInstance(KontaktopplysningerRequest.class);
        this.responseContext = JAXBContext.newInstance(KontaktopplysningerResponse.class);
    }

    public Map<String, TpsMeldingResponse> sendKontaktopplysninger(String ident, KontaktopplysningerRequestDTO kontaktopplysninger, List<String> miljoer) {

        try {
            var context = new MappingContext.Factory().getContext();
            context.setProperty("ident", ident);
            context.setProperty("datoSprak", nonNull(kontaktopplysninger.getEndringAvSprak()) ?
                    kontaktopplysninger.getEndringAvSprak().getEndreSprak().getDatoSprak() : null);

            var request = mapperFacade.map(kontaktopplysninger, KontaktopplysningerRequest.class, context);
            var requestXml = marshallToXML(requestContext, request);
            var miljoerResponse = endringsmeldingConsumer.sendMessage(requestXml, miljoer);

            return miljoerResponse.entrySet().stream()
                    .collect(Collectors.toMap(Entry::getKey, entry -> {

                        try {
                            var response = (KontaktopplysningerResponse) unmarshallFromXml(responseContext, entry.getValue());
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
