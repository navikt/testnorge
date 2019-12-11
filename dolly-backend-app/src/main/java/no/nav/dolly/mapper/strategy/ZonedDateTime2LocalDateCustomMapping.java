package no.nav.dolly.mapper.strategy;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@Slf4j
@Component
public class ZonedDateTime2LocalDateCustomMapping extends BidirectionalConverter<ZonedDateTime, LocalDate> {

    @Override public LocalDate convertTo(ZonedDateTime zonedDateTime, Type<LocalDate> type, MappingContext mappingContext) {
        return zonedDateTime.toLocalDate();
    }

    @Override public ZonedDateTime convertFrom(LocalDate localDate, Type<ZonedDateTime> type, MappingContext mappingContext) {
        return localDate.atStartOfDay().atZone(ZoneId.systemDefault());
    }
}