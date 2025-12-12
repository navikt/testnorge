package no.nav.testnav.libs.dto.jackson.v1;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Arrays;

public class CaseInsensitiveEnumDeserializer extends StdDeserializer<Enum<?>> implements ContextualDeserializer {

    private Class<? extends Enum> enumClass;

    public CaseInsensitiveEnumDeserializer() {
        super(Enum.class);
    }

    public CaseInsensitiveEnumDeserializer(Class<? extends Enum> enumClass) {
        super(enumClass);
        this.enumClass = enumClass;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) throws JsonMappingException {
        Class<?> rawClass = context.getContextualType().getRawClass();
        if (rawClass.isEnum()) {
            return new CaseInsensitiveEnumDeserializer((Class<? extends Enum>) rawClass);
        }
        return this;
    }

    @Override
    public Enum<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();
        if (value == null || value.isEmpty()) {
            return null;
        }

        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        
        return Arrays.stream(enumConstants)
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseGet(() -> Arrays.stream(enumConstants)
                        .filter(e -> {
                            try {
                                var toStringMethod = enumClass.getMethod("toString");
                                return toStringMethod.invoke(e).toString().equalsIgnoreCase(value);
                            } catch (Exception ex) {
                                return false;
                            }
                        })
                        .findFirst()
                        .orElseGet(() -> Arrays.stream(enumConstants)
                                .filter(e -> {
                                    try {
                                        var getValueMethod = enumClass.getMethod("getValue");
                                        return getValueMethod.invoke(e).toString().equalsIgnoreCase(value);
                                    } catch (Exception ex) {
                                        return false;
                                    }
                                })
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "No enum constant " + enumClass.getName() + " for value: " + value))
                        )
                );
    }
}


