package no.nav.pdl.forvalter.database;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@WritingConverter
@RequiredArgsConstructor
public class PersonDTOToJsonConverter implements Converter<PersonDTO, Json> {

    private final JsonMapper jsonMapper;

    @Override
    public Json convert(PersonDTO source) {
        try {
            return Json.of(jsonMapper.writeValueAsString(source));
        } catch (Exception e) {
            log.error("Feil ved konvertering av PersonDTO til JSON: {}", e.getMessage(), e);
            throw new IllegalStateException("Feil ved konvertering av PersonDTO til JSON", e);
        }
    }
}