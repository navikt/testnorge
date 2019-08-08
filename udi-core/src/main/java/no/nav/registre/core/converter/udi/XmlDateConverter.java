package no.nav.registre.core.converter.udi;

import no.nav.registre.core.converter.BaseConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Date;

@Component
public class XmlDateConverter extends BaseConverter<Date, XMLGregorianCalendar> {

    private final DatatypeFactory factory;

    public XmlDateConverter() throws DatatypeConfigurationException {
        this.factory = DatatypeFactory.newInstance();
    }

    @Override
    public XMLGregorianCalendar convert(@Nullable Date date) {
        return date == null ? null : factory.newXMLGregorianCalendar(date.toString());
    }
}
