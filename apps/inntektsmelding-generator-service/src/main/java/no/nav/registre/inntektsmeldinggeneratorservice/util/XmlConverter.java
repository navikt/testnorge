package no.nav.registre.inntektsmeldinggeneratorservice.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntektsmeldinggeneratorservice.exception.JaxbToXmlException;
import org.apache.commons.text.CaseUtils;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Slf4j
public class XmlConverter {

    private XmlConverter() {

    }

    public static <T> String toXml(JAXBElement<T> value, Class<T> clazz) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("no.nav.registre.inntektsmeldinggeneratorservice.v20181211.adapter", clazz.getClassLoader());

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);


            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(value, sw);

            String xmlContent = sw.toString().replace("ns2:", "").replace(":ns2", "");

            log.debug("Opprettet xml: {}", xmlContent);
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

    public static BigDecimal toBigDecimal(Double value) {

        return nonNull(value) ? BigDecimal.valueOf(value) : null;
    }

    public static BigInteger toBigInteger(Integer value) {

        return nonNull(value) ? BigInteger.valueOf(value) : null;
    }

    public static String toCamelCase(String value) {

        return nonNull(value) ? CaseUtils.toCamelCase(value, true, '_') : null;
    }

    public static LocalDate toLocalDate(LocalDateTime localDateTime) {

        return nonNull(localDateTime) ? localDateTime.toLocalDate() : null;
    }

    @SuppressWarnings("unchecked")
    private static <T> void toObject(String xml, Class<T> clazz) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("no.nav.registre.inntektsmeldinggeneratorservice.v20181211.adapter");

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            unmarshaller.unmarshal(new StringReader(xml));

        } catch (JAXBException e) {
            throw new JaxbToXmlException("klarte ikke å konvertere Jaxb element til Objekt", e);
        }
    }
}
