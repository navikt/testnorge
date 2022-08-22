package no.nav.testnav.apps.tpsmessagingservice.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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

    public static TpsMeldingResponse getResponseStatus(EndringsmeldingResponse response) {

        if (nonNull(response)) {
            log.info("Svarmelding mottatt fra TPS: {}", response);
            return TpsMeldingResponse.builder()
                    .returStatus(isStatusOk(response.getSfeTilbakeMelding().getSvarStatus().getReturStatus()) ? STATUS_OK : STATUS_ERROR)
                    .returMelding(response.getSfeTilbakeMelding().getSvarStatus().getReturMelding())
                    .utfyllendeMelding(response.getSfeTilbakeMelding().getSvarStatus().getUtfyllendeMelding())
                    .build();
        } else {
            return getNoAnswerStatus();
        }
    }

    public static TpsMeldingResponse getErrorStatus(JAXBException e) {

        return TpsMeldingResponse.builder()
                .returStatus(STATUS_ERROR)
                .returMelding(e.getMessage())
                .utfyllendeMelding(e.getLocalizedMessage())
                .build();
    }

    @SneakyThrows
    public static String marshallToXML(JAXBContext requestContext, EndringsmeldingRequest endringsmelding) {

        var marshaller = requestContext.createMarshaller();

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

    public static TpsMeldingResponse getNoAnswerStatus() {

        return TpsMeldingResponse.builder()
                .returStatus("FEIL")
                .returMelding("Teknisk feil!")
                .utfyllendeMelding("Ingen svarstatus mottatt fra TPS")
                .build();
    }
}
