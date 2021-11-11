package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.EndringsmeldingResponseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KontaktopplysningerService {

    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "FEIL";

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

    private static boolean isStatusOk(String status) {

        return "00".equals(status) || "04".equals(status);
    }

    private String marshallToXML(KontaktopplysningerRequest kontaktInfo) throws JAXBException {

        var marshaller = requestContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        var writer = new StringWriter();
        marshaller.marshal(kontaktInfo, writer);

        return writer.toString();
    }

    private KontaktopplysningerResponse unmarshallFromXml(String kontaktopplysningerResponse) throws JAXBException {

        var unmarshaller = responseContext.createUnmarshaller();
        var reader = new StringReader(kontaktopplysningerResponse);
        return (KontaktopplysningerResponse) unmarshaller.unmarshal(reader);
    }

    public Map<String, EndringsmeldingResponseDTO> sendKontaktopplysninger(String ident, KontaktopplysningerRequestDTO kontaktopplysninger, List<String> miljoer) {

        try {
            var context = new MappingContext.Factory().getContext();
            context.setProperty("ident", ident);

            var request = mapperFacade.map(kontaktopplysninger, KontaktopplysningerRequest.class, context);
            var requestXml = marshallToXML(request);
            var miljoerResponse = endringsmeldingConsumer.sendEndringsmelding(requestXml, miljoer);

            return miljoerResponse.entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey(), entry -> {

                        try {
                            var response = unmarshallFromXml(entry.getValue());
                            return EndringsmeldingResponseDTO.builder()
                                    .returStatus(isStatusOk(response.getSfeTilbakemelding().getSvarStatus().getReturStatus()) ? STATUS_OK : STATUS_ERROR)
                                    .returMelding(response.getSfeTilbakemelding().getSvarStatus().getReturMelding())
                                    .utfyllendeMelding(response.getSfeTilbakemelding().getSvarStatus().getUtfyllendeMelding())
                                    .build();

                        } catch (JAXBException e) {
                            log.error(e.getMessage(), e);
                            return EndringsmeldingResponseDTO.builder()
                                    .returStatus(STATUS_ERROR)
                                    .returMelding(e.getErrorCode())
                                    .utfyllendeMelding(e.getMessage())
                                    .build();
                        }
                    }));

        } catch (JAXBException e) {

            log.error(e.getMessage(), e);
            return Map.of("NA", EndringsmeldingResponseDTO.builder()
                    .returStatus(STATUS_ERROR)
                    .returMelding(e.getErrorCode())
                    .utfyllendeMelding(e.getMessage())
                    .build());
        }
    }
}
