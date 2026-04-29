package no.nav.registre.testnorge.organisasjonfastedataservice.repository.converter;

import jakarta.persistence.AttributeConverter;
import lombok.SneakyThrows;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class OrganisasjonJsonConverter implements AttributeConverter<Organisasjon, String> {
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(Organisasjon attribute) {
        return objectMapper.writeValueAsString(attribute);
    }

    @SneakyThrows
    @Override
    public Organisasjon convertToEntityAttribute(String dbData) {
        return objectMapper.readValue(dbData, Organisasjon.class);
    }
}