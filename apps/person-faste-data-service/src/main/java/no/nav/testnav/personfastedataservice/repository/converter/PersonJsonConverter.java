package no.nav.testnav.personfastedataservice.repository.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;

import no.nav.testnav.personfastedataservice.domain.Person;

public class PersonJsonConverter implements AttributeConverter<Person, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

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