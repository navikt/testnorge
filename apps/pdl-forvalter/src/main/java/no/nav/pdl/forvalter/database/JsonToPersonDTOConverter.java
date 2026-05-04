package no.nav.pdl.forvalter.database;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@ReadingConverter
@RequiredArgsConstructor
public class JsonToPersonDTOConverter implements Converter<Json, PersonDTO> {

    private final JsonMapper jsonMapper;

    @Override
    public PersonDTO convert(Json source) {
        try {
            return jsonMapper.readValue(source.asString(), PersonDTO.class);
        } catch (Exception e) {
            log.error("Feil ved konvertering av JSON til PersonDTO: {}", e.getMessage(), e);
            throw new IllegalStateException("Feil ved konvertering av JSON til PersonDTO", e);
        }
    }
}