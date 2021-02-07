package no.nav.udistub.converter.ws;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

@Component
public class XmlDateWsConverter implements Converter<LocalDate, XMLGregorianCalendar> {

    @Override
    public XMLGregorianCalendar convert(@Nullable LocalDate localDate) {

        if (localDate == null) {
            return null;
        }
        try {
            LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);

            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            xmlGregorianCalendar.setYear(localDateTime.getYear());
            xmlGregorianCalendar.setMonth(localDateTime.getMonthValue());
            xmlGregorianCalendar.setDay(localDateTime.getDayOfMonth());

            xmlGregorianCalendar.setTime(localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());

            xmlGregorianCalendar.setTimezone(TimeZone.getDefault().getRawOffset() / 1000 / 60);

            return xmlGregorianCalendar;
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
