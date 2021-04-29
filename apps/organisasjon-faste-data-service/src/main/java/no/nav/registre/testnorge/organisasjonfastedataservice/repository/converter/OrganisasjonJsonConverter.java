package no.nav.registre.testnorge.organisasjonfastedataservice.repository.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;

import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;

public class OrganisasjonJsonConverter implements AttributeConverter<Organisasjon, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

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