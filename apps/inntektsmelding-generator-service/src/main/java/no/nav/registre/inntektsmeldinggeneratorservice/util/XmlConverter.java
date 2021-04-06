package no.nav.registre.inntektsmeldinggeneratorservice.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.inntektsmeldinggeneratorservice.exception.JaxbToXmlException;

@Slf4j
public class XmlConverter {

    private XmlConverter() {

    }

    public static <T> String toXml(JAXBElement<T> value, Class<T> clazz) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(value, sw);

            String xmlContent = sw.toString();

            if (log.isDebugEnabled()) {
                log.debug("Opprettet xml: {}", xmlContent);
            }
            return xmlContent;
        } catch (JAXBException e) {
            throw new JaxbToXmlException("klarte ikke å konvertere Jaxb element til XML", e);
        }
    }

    public static <T> boolean validate(String xml, Class<T> clazz) {
        try {
            toObject(xml, clazz);
            return true;
        } catch (Exception e) {
            log.warn("Validering av xml feilet", e);
            return false;
        }
    }


    @SuppressWarnings("unchecked")
    private static <T> T toObject(String xml, Class<T> clazz) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            return (T) unmarshaller.unmarshal(new StringReader(xml));

        } catch (JAXBException e) {
            throw new JaxbToXmlException("klarte ikke å konvertere Jaxb element til Objekt", e);
        }
    }
}
