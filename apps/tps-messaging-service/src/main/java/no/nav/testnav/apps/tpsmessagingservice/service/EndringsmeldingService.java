package no.nav.testnav.apps.tpsmessagingservice.service;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndringsmeldingService {

    private final EndringsmeldingConsumer endringsmeldingConsumer;

    public Map<String, String> sendKontaktopplysninger(KontaktopplysningerRequestDTO kontaktopplysninger, List<String> miljoer) {

        try {
            var xml = jaxbKontanktInformasjonToXML(kontaktopplysninger);
            return endringsmeldingConsumer.sendEndringsmelding(xml, List.of("Q4"));

        } catch (JAXBException e) {

            log.error(e.getMessage(), e);
            return Map.of("NA", e.getMessage());
        }
    }

    private static String jaxbKontanktInformasjonToXML(KontaktopplysningerRequestDTO kontaktInfo) throws JAXBException {

            var context = JAXBContext.newInstance(KontaktopplysningerRequestDTO.class);
            var marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

            var stringWriter = new StringWriter();
            marshaller.marshal(kontaktInfo, stringWriter);

            return stringWriter.toString();
    }
}
