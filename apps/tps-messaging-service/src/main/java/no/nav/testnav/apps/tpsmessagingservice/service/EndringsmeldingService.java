package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndringsmeldingService {

    private final MapperFacade mapperFacade;
    private final EndringsmeldingConsumer endringsmeldingConsumer;

    private static String jaxbKontanktInformasjonToXML(KontaktopplysningerRequest kontaktInfo) throws JAXBException {

        var context = JAXBContext.newInstance(KontaktopplysningerRequest.class);
        var marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

        var stringWriter = new StringWriter();
        marshaller.marshal(kontaktInfo, stringWriter);

        return stringWriter.toString();
    }

    public Map<String, String> sendKontaktopplysninger(KontaktopplysningerRequestDTO kontaktopplysninger, List<String> miljoer) {

        try {
            var request = mapperFacade.map(kontaktopplysninger, KontaktopplysningerRequest.class);
            var xml = jaxbKontanktInformasjonToXML(request);
            return endringsmeldingConsumer.sendEndringsmelding(xml, List.of("Q4"));

        } catch (JAXBException e) {

            log.error(e.getMessage(), e);
            return Map.of("NA", e.getMessage());
        }
    }
}
