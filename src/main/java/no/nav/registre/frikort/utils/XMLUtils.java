package no.nav.registre.frikort.utils;

import no.nav.registre.frikort.domain.xml.Egenandelsmelding;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class XMLUtils {

    public static String convertEgenandelsmeldingToXMLString(Egenandelsmelding egenandelsmelding) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Egenandelsmelding.class);

        Marshaller marshaller = context.createMarshaller();

        // enable pretty-print XML output
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // set encoding property
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");

        StringWriter sw = new StringWriter();

        // convert object to XML
        marshaller.marshal(egenandelsmelding, sw);

        return sw.toString();
    }

}
