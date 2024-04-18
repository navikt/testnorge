package no.nav.testnav.apps.tpsmessagingservice.utils;

import jakarta.xml.bind.JAXBContext;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsRequest;

import java.io.StringWriter;

@UtilityClass
public class ServiceRutineUtil {

    @SneakyThrows
    public static String marshallToXML(JAXBContext requestContext, TpsRequest melding) {

        var marshaller = requestContext.createMarshaller();

        var writer = new StringWriter();
        marshaller.marshal(melding, writer);

        return writer.toString();
    }
}
