package no.nav.dolly.mapper.strategy;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
public class LocalDateCustomMapping extends BidirectionalConverter<LocalDateTime, LocalDate> {

    @Override
    public LocalDate convertTo(LocalDateTime localDateTime, Type<LocalDate> type, MappingContext mappingContext) {
        return localDateTime.toLocalDate();
    }

    @Override
    public LocalDateTime convertFrom(LocalDate localDate, Type<LocalDateTime> type, MappingContext mappingContext) {
        return localDate.atStartOfDay();
    }
}