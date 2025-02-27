package no.nav.testnav.oppdragservice.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LocalDateTimeCustomMapping extends BidirectionalConverter<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSSSSS");

    @Override
    public String convertTo(LocalDateTime localDateTime, Type<String> type, MappingContext mappingContext) {

        return FORMATTER.format(localDateTime);
    }

    @Override
    public LocalDateTime convertFrom(String s, Type<LocalDateTime> type, MappingContext mappingContext) {

        return LocalDateTime.from(FORMATTER.parse(s));
    }
}