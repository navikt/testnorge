package no.nav.testnav.oppdragservice.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LocalDateCustomMapping extends BidirectionalConverter<LocalDate, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public String convertTo(LocalDate localDate, Type<String> type, MappingContext mappingContext) {

        return FORMATTER.format(localDate);
    }

    @Override
    public LocalDate convertFrom(String s, Type<LocalDate> type, MappingContext mappingContext) {

        return LocalDate.from(FORMATTER.parse(s));
    }
}