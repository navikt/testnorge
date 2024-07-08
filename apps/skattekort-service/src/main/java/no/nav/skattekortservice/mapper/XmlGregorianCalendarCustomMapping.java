package no.nav.skattekortservice.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

@Slf4j
@Component
public class XmlGregorianCalendarCustomMapping extends BidirectionalConverter<XMLGregorianCalendar, LocalDate> {

    @Override
    public LocalDate convertTo(XMLGregorianCalendar calendar, Type<LocalDate> type, MappingContext mappingContext) {

        return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
    }

    @Override
    public XMLGregorianCalendar convertFrom(LocalDate dateTime, Type<XMLGregorianCalendar> type, MappingContext mappingContext) {

        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            xmlGregorianCalendar.setYear(dateTime.getYear());
            xmlGregorianCalendar.setMonth(dateTime.getMonthValue());
            xmlGregorianCalendar.setDay(dateTime.getDayOfMonth());

            return xmlGregorianCalendar;

        } catch (DatatypeConfigurationException e) {
            log.debug("XmlGregorianCalendar to LocalDateTime conversion error", e);
        }
        return null;
    }
}
