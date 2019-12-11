package no.nav.dolly.mapper.strategy;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@Slf4j
@Component
public class ZonedDateTime2LocalDateTimeCustomMapping extends BidirectionalConverter<ZonedDateTime, LocalDateTime> {

    @Override public LocalDateTime convertTo(ZonedDateTime zonedDateTime, Type<LocalDateTime> type, MappingContext mappingContext) {
        return zonedDateTime.toLocalDateTime();
    }

    @Override public ZonedDateTime convertFrom(LocalDateTime localDateTime, Type<ZonedDateTime> type, MappingContext mappingContext) {
        return localDateTime.atZone(ZoneId.systemDefault());
    }
}