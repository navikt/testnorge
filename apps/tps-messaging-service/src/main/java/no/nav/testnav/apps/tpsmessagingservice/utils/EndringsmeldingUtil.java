package no.nav.testnav.apps.tpsmessagingservice.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.EndringsmeldingResponseDTO;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringReader;
import java.io.StringWriter;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@UtilityClass
public class EndringsmeldingUtil {

    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "FEIL";

    public static boolean isStatusOk(String status) {

        return "00".equals(status) || "04".equals(status);
    }

    public static EndringsmeldingResponseDTO getResponseStatus(EndringsmeldingResponse response) {

        if (nonNull(response)) {
            log.info("Svarmelding mottatt fra TPS: {}", response);
            return EndringsmeldingResponseDTO.builder()
                    .returStatus(isStatusOk(response.getSfeTilbakemelding().getSvarStatus().getReturStatus()) ? STATUS_OK : STATUS_ERROR)
                    .returMelding(response.getSfeTilbakemelding().getSvarStatus().getReturMelding())
                    .utfyllendeMelding(response.getSfeTilbakemelding().getSvarStatus().getUtfyllendeMelding())
                    .build();
        } else {
            return EndringsmeldingResponseDTO.builder()
                    .returStatus(STATUS_ERROR)
                    .returMelding("Teknisk feil!")
                    .utfyllendeMelding("Ingen svarstatus mottatt fra TPS")
                    .build();
        }
    }

    public static EndringsmeldingResponseDTO getErrorStatus(JAXBException e) {

        return EndringsmeldingResponseDTO.builder()
                .returStatus(STATUS_ERROR)
                .returMelding(e.getErrorCode())
                .utfyllendeMelding(e.getMessage())
                .build();
    }

    public static String marshallToXML(JAXBContext requestContext, EndringsmeldingRequest endringsmelding) throws JAXBException {

        var marshaller = requestContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        var writer = new StringWriter();
        marshaller.marshal(endringsmelding, writer);

        return writer.toString();
    }

    public static Object unmarshallFromXml(JAXBContext responseContext, String endringsmeldingResponse) throws JAXBException {

        if (isNotBlank(endringsmeldingResponse)) {

            var unmarshaller = responseContext.createUnmarshaller();
            var reader = new StringReader(endringsmeldingResponse);

            return unmarshaller.unmarshal(reader);

        } else {

            return null;
        }
    }
}
