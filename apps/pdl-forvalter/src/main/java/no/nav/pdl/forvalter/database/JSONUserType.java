package no.nav.pdl.forvalter.database;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.domain.PersonDTO;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.springframework.web.client.HttpServerErrorException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
public class JSONUserType implements UserType {

    private final ObjectMapper objectMapper;

    public JSONUserType() {
        objectMapper = new ObjectMapper();
        objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        var simpleModule = new SimpleModule();
        simpleModule.addDeserializer(LocalDateTime.class, new TestnavLocalDateTimeDeserializer());
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        simpleModule.addDeserializer(LocalDate.class, new TestnavLocalDateDeserializer());
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
        simpleModule.addDeserializer(ZonedDateTime.class, new TestnavZonedDateTimeDeserializer());
        simpleModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));

        objectMapper.registerModule(simpleModule);
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.JAVA_OBJECT};
    }

    @Override
    public Class<PersonDTO> returnedClass() {
        return PersonDTO.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {

        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {

        var cellContent = rs.getString(names[0]);
        if (cellContent == null) {
            return null;
        }
        try {
            return objectMapper.readValue(cellContent.getBytes(StandardCharsets.UTF_8), returnedClass());
        } catch (final Exception ex) {
            log.error("Kunne ikke mappe String til PdlPerson: {}", ex.getMessage(), ex);
            throw new HttpServerErrorException(INTERNAL_SERVER_ERROR, "Kunne ikke mappe String til PdlPerson: " + ex.getMessage());
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement ps, Object value, int idx, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            ps.setNull(idx, Types.OTHER);
            return;
        }
        try {
            var writer = new StringWriter();
            objectMapper.writeValue(writer, value);
            writer.flush();
            ps.setObject(idx, writer.toString(), Types.OTHER);
        } catch (final Exception ex) {
            log.error("Kunne ikke mappe PdlPerson til String {}", ex.getMessage(), ex);
            throw new HttpServerErrorException(INTERNAL_SERVER_ERROR, "Kunne ikke mappe PdlPerson til String: " + ex.getMessage());
        }

    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {

        try {
            // use serialization to create a deep copy
            var bos = new ByteArrayOutputStream();
            var oos = new ObjectOutputStream(bos);
            oos.writeObject(value);
            oos.flush();
            oos.close();
            bos.close();

            var bais = new ByteArrayInputStream(bos.toByteArray());
            return new ObjectInputStream(bais).readObject();
        } catch (ClassNotFoundException | IOException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) this.deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return this.deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return this.deepCopy(original);
    }

    private static class TestnavZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

        @Override
        public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            return ZonedDateTime.parse(node.asText(), DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    private static class TestnavLocalDateDeserializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            var dateTime = node.asText().length() > 10 ? node.asText().substring(0, 10) : node.asText();
            return LocalDate.parse(dateTime);
        }
    }

    private static class TestnavLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (isBlank(node.asText())) {
                return null;
            }
            var dateTime = node.asText().length() > 19 ? node.asText().substring(0, 19) : node.asText();
            return dateTime.length() > 10 ? LocalDateTime.parse(dateTime) : LocalDate.parse(dateTime).atStartOfDay();
        }
    }
}