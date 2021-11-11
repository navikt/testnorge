package no.nav.testnav.apps.tpsmessagingservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.EndringsmeldingResponseDTO;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringReader;
import java.io.StringWriter;

@UtilityClass
public class EndringsmeldingUtil {

    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "FEIL";

    public static boolean isStatusOk(String status) {

        return "00".equals(status) || "04".equals(status);
    }

    public static EndringsmeldingResponseDTO getOkeyStatus(EndringsmeldingResponseDTO response) {

        return EndringsmeldingResponseDTO.builder()
                .returStatus(isStatusOk(response.getReturStatus()) ? STATUS_OK : STATUS_ERROR)
                .returMelding(response.getReturMelding())
                .utfyllendeMelding(response.getUtfyllendeMelding())
                .build();
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

    public static Object unmarshallFromXml(JAXBContext responseContext, String kontaktopplysningerResponse) throws JAXBException {

        var unmarshaller = responseContext.createUnmarshaller();
        var reader = new StringReader(kontaktopplysningerResponse);
        return unmarshaller.unmarshal(reader);
    }
}
