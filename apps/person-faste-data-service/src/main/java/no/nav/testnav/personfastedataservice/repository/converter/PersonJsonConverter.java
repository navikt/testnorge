package no.nav.testnav.personfastedataservice.repository.converter;

import jakarta.persistence.AttributeConverter;
import lombok.SneakyThrows;
import no.nav.testnav.personfastedataservice.domain.Person;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class PersonJsonConverter implements AttributeConverter<Person, String> {
    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(Person attribute) {
        return objectMapper.writeValueAsString(attribute);
    }

    @SneakyThrows
    @Override
    public Person convertToEntityAttribute(String dbData) {
        return objectMapper.readValue(dbData, Person.class);
    }
}