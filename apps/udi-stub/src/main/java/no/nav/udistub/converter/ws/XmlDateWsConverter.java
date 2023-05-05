package no.nav.udistub.converter.ws;

import lombok.extern.slf4j.Slf4j;
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class XmlDateWsConverter implements Converter<LocalDate, XMLGregorianCalendar> {

    @Override
    public XMLGregorianCalendar convert(@Nullable LocalDate localDate) {

        if (isNull(localDate)) {
            return null;
        }
        try {
            var localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);

            var xmlGregorianCalendar = getXmlGregorianCalendar(localDateTime);
            xmlGregorianCalendar.setTime(localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());

            return xmlGregorianCalendar;

        } catch (DatatypeConfigurationException e) {
            log.error("Kunne ikke konvertere datatype: ", e);
        }
        return null;
    }

    public XMLGregorianCalendar convert(@Nullable LocalDateTime localDateTime) {

        if (isNull(localDateTime)) {
            return null;
        }
        try {
            var xmlGregorianCalendar = getXmlGregorianCalendar(localDateTime);
            xmlGregorianCalendar.setTime(localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());

            return xmlGregorianCalendar;

        } catch (DatatypeConfigurationException e) {
            log.error("Kunne ikke konvertere datatype: ", e);
        }
        return null;
    }

    private static XMLGregorianCalendar getXmlGregorianCalendar(LocalDateTime localDateTime) throws DatatypeConfigurationException {

        var xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        xmlGregorianCalendar.setYear(localDateTime.getYear());
        xmlGregorianCalendar.setMonth(localDateTime.getMonthValue());
        xmlGregorianCalendar.setDay(localDateTime.getDayOfMonth());
        xmlGregorianCalendar.setTimezone(TimeZone.getDefault().getRawOffset() / 1000 / 60);

        return xmlGregorianCalendar;
    }
}
