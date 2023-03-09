package no.nav.testnav.apps.tpsmessagingservice.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineRequest;

import javax.xml.bind.JAXBContext;
import java.io.StringWriter;

@UtilityClass
public class ServiceRutineUtil {

    @SneakyThrows
    public static String marshallToXML(JAXBContext requestContext, TpsServicerutineRequest endringsmelding) {

        var marshaller = requestContext.createMarshaller();

        var writer = new StringWriter();
        marshaller.marshal(endringsmelding, writer);

        return writer.toString();
    }
}
