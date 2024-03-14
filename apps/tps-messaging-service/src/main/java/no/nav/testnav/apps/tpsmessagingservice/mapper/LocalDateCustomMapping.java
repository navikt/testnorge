package no.nav.testnav.apps.tpsmessagingservice.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class LocalDateCustomMapping extends BidirectionalConverter<String, LocalDate> {

    @Override
    public LocalDate convertTo(String localDate, Type<LocalDate> type, MappingContext mappingContext) {

        return isNotBlank(localDate) ? LocalDate.parse(localDate) : null;
    }

    @Override
    public String convertFrom(LocalDate localDate, Type<String> type, MappingContext mappingContext) {

        return nonNull(localDate) ? localDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }
}