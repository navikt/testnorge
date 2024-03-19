package no.nav.testnav.apps.tpsmessagingservice.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
public class LocalDateTimeCustomMapping extends BidirectionalConverter<String, LocalDateTime> {

    @Override
    public LocalDateTime convertTo(String localDateTime, Type<LocalDateTime> type, MappingContext mappingContext) {

        if (isBlank(localDateTime)) {
            return null;
        }

        return localDateTime.length() == 10 ?
                LocalDate.parse(localDateTime).atStartOfDay() :
                LocalDateTime.parse(localDateTime);
    }

    @Override
    public String convertFrom(LocalDateTime localDateTime, Type<String> type, MappingContext mappingContext) {

        return nonNull(localDateTime) ? localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }
}