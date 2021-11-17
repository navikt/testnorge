package no.nav.testnav.apps.tpsmessagingservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringWriter;

@UtilityClass
public class ServiceRutineUtil {

    public static String marshallToXML(JAXBContext requestContext, TpsServicerutineRequest endringsmelding) throws JAXBException {

        var marshaller = requestContext.createMarshaller();

        var writer = new StringWriter();
        marshaller.marshal(endringsmelding, writer);

        return writer.toString();
    }
}
