package no.nav.pdl.forvalter.database.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@Slf4j
@ReadingConverter
@RequiredArgsConstructor
public class PersonReadConverter implements Converter<Json, PersonDTO> {

    private final ObjectMapper objectMapper;

    @Override
    public PersonDTO convert(Json source) {

        try {
            return objectMapper.readValue(source.asString(), PersonDTO.class);

        } catch (JsonProcessingException e) {log.error(e.getMessage(), e);
            return null;
        }
    }
}