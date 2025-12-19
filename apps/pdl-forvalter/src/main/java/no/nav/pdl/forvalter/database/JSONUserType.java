package no.nav.pdl.forvalter.database;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.exception.InternalServerException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.SimpleFilterProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class JSONUserType implements UserType<PersonDTO> {

    private final ObjectMapper objectMapper;

    public JSONUserType() {
        var simpleModule = new SimpleModule();
        simpleModule.addDeserializer(LocalDateTime.class, new TestnavLocalDateTimeDeserializer());
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        simpleModule.addDeserializer(LocalDate.class, new TestnavLocalDateDeserializer());
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer());

        objectMapper = JsonMapper.builder()
                .addModule(simpleModule)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(MapperFeature.USE_GETTERS_AS_SETTERS, false)
                .filterProvider(new SimpleFilterProvider().setFailOnUnknownId(false))
                .build();
    }

    @Override
    public int getSqlType() {
        return Types.JAVA_OBJECT;
    }

    @Override
    public Class<PersonDTO> returnedClass() {
        return PersonDTO.class;
    }

    @Override
    public boolean equals(PersonDTO x, PersonDTO y) throws HibernateException {

        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(PersonDTO x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public PersonDTO nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {

        var cellContent = rs.getString(position);
        if (cellContent == null) {
            return null;
        }
        try {
            return objectMapper.readValue(cellContent.getBytes(StandardCharsets.UTF_8), returnedClass());
        } catch (final Exception ex) {
            log.error("Kunne ikke mappe String til PdlPerson: {}", ex.getMessage(), ex);
            throw new InternalServerException("Kunne ikke mappe String til PdlPerson: " + ex.getMessage());
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement ps, PersonDTO value, int idx, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            ps.setNull(idx, Types.OTHER);
            return;
        }
        try {
            var jsonString = objectMapper.writeValueAsString(value);
            ps.setObject(idx, jsonString, Types.OTHER);
        } catch (final Exception ex) {
            log.error("Kunne ikke mappe PdlPerson til String {}", ex.getMessage(), ex);
            throw new InternalServerException("Kunne ikke mappe PdlPerson til String: " + ex.getMessage());
        }

    }

    @Override
    public PersonDTO deepCopy(PersonDTO value) throws HibernateException {

        try {
            // use serialization to create a deep copy
            var bos = new ByteArrayOutputStream();
            var oos = new ObjectOutputStream(bos);
            oos.writeObject(value);
            oos.flush();
            oos.close();
            bos.close();

            var bais = new ByteArrayInputStream(bos.toByteArray());
            return (PersonDTO) new ObjectInputStream(bais).readObject();
        } catch (ClassNotFoundException | IOException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(PersonDTO value) throws HibernateException {
        return this.deepCopy(value);
    }

    @Override
    public PersonDTO assemble(Serializable cached, Object owner) throws HibernateException {
        return this.deepCopy((PersonDTO) cached);
    }

    @Override
    public PersonDTO replace(PersonDTO original, PersonDTO target, Object owner) throws HibernateException {
        return this.deepCopy(original);
    }

    private static class LocalDateSerializer extends ValueSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializationContext context) {
            gen.writeString(value.format(DateTimeFormatter.ISO_DATE));
        }
    }

    private static class LocalDateTimeSerializer extends ValueSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext context) {
            gen.writeString(value.format(DateTimeFormatter.ISO_DATE_TIME));
        }
    }

    private static class TestnavLocalDateDeserializer extends ValueDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            if (isBlank(node.asText())) {
                return null;
            }
            var dateTime = node.asText().length() > 10 ? node.asText().substring(0, 10) : node.asText();
            return LocalDate.parse(dateTime);
        }
    }

    private static class TestnavLocalDateTimeDeserializer extends ValueDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            JsonNode node = jsonParser.readValueAsTree();
            if (isBlank(node.asText())) {
                return null;
            }
            var dateTime = node.asText().length() > 19 ? node.asText().substring(0, 19) : node.asText();
            return dateTime.length() > 10 ? LocalDateTime.parse(dateTime) : LocalDate.parse(dateTime).atStartOfDay();
        }
    }
}