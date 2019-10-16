package no.nav.dolly.mapper.strategy;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;

@Slf4j
@Component
public class XmlGregorianCalendarCustomMapping extends BidirectionalConverter<XMLGregorianCalendar, LocalDateTime> {

    @Override
    public LocalDateTime convertTo(XMLGregorianCalendar calendar, Type<LocalDateTime> type, MappingContext mappingContext) {

        return LocalDateTime.of(calendar.getYear(), calendar.getMonth(), calendar.getDay(), calendar.getHour(), calendar.getMinute(), calendar.getSecond());
    }

    @Override
    public XMLGregorianCalendar convertFrom(LocalDateTime dateTime, Type<XMLGregorianCalendar> type, MappingContext mappingContext) {

        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            xmlGregorianCalendar.setYear(dateTime.getYear());
            xmlGregorianCalendar.setMonth(dateTime.getMonthValue());
            xmlGregorianCalendar.setDay(dateTime.getDayOfMonth());
            xmlGregorianCalendar.setHour(dateTime.getHour());
            xmlGregorianCalendar.setMinute(dateTime.getMinute());
            xmlGregorianCalendar.setSecond(dateTime.getSecond());

            return xmlGregorianCalendar;

        } catch (DatatypeConfigurationException e) {
            log.debug("XmlGregorianCalendar to LocalDateTime conversion error", e);
        }
        return null;
    }
}
