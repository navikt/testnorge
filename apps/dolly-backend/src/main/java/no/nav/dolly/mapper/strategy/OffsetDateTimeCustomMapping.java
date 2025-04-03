package no.nav.dolly.mapper.strategy;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Slf4j
@Component
public class OffsetDateTimeCustomMapping extends BidirectionalConverter<OffsetDateTime, LocalDateTime> {

    @Override
    public LocalDateTime convertTo(OffsetDateTime source, Type<LocalDateTime> type, MappingContext mappingContext) {
        return source.toLocalDateTime();
    }

    @Override
    public OffsetDateTime convertFrom(LocalDateTime source, Type<OffsetDateTime> type, MappingContext mappingContext) {
        return OffsetDateTime.of(source, OffsetDateTime.now(ZoneId.of("Europe/Oslo")).getOffset());
    }
}