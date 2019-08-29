package no.nav.registre.udistub.core.converter;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class LocalDateCustomMapping extends BidirectionalConverter<LocalDate, LocalDate> {

    @Override
    public LocalDate convertTo(LocalDate localDate, Type<LocalDate> type, MappingContext mappingContext) {
        return localDate;
    }

    @Override
    public LocalDate convertFrom(LocalDate localDate, Type<LocalDate> type, MappingContext mappingContext) {
        return localDate;
    }
}