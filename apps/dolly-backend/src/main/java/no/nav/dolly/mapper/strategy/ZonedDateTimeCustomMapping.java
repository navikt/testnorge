package no.nav.dolly.mapper.strategy;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Component
public class ZonedDateTimeCustomMapping extends BidirectionalConverter<ZonedDateTime, LocalDateTime> {

    @Override
    public LocalDateTime convertTo(ZonedDateTime source, Type<LocalDateTime> destinationType, MappingContext mappingContext) {
        return source.toLocalDateTime();
    }

    @Override
    public ZonedDateTime convertFrom(LocalDateTime source, Type<ZonedDateTime> destinationType, MappingContext mappingContext) {
        return source.atZone(ZoneId.of("Europe/Oslo"));
    }
}