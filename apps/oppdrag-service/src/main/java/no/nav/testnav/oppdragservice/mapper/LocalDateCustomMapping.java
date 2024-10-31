package no.nav.testnav.oppdragservice.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LocalDateCustomMapping extends CustomConverter<LocalDate, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public String convert(LocalDate localDate, Type<? extends String> type, MappingContext mappingContext) {

        return FORMATTER.format(localDate);
    }
}