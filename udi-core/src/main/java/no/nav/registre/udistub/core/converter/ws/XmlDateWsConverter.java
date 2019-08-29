package no.nav.registre.udistub.core.converter.ws;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

@Component
public class XmlDateWsConverter implements Converter<LocalDate, XMLGregorianCalendar> {

    private final DatatypeFactory factory;

    public XmlDateWsConverter() throws DatatypeConfigurationException {
        this.factory = DatatypeFactory.newInstance();
    }

    @Override
    public XMLGregorianCalendar convert(@Nullable LocalDate date) {
        return date == null ? null : factory.newXMLGregorianCalendar(date.toString());
    }
}
