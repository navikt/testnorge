package no.nav.testnav.libs.dto.jackson.v1;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdScalarDeserializer;

import java.util.Arrays;

public class CaseInsensitiveEnumDeserializer extends StdScalarDeserializer<Enum<?>> {

    private Class<? extends Enum> enumClass;

    public CaseInsensitiveEnumDeserializer() {
        super(Enum.class);
    }

    public CaseInsensitiveEnumDeserializer(Class<? extends Enum> enumClass) {
        super(enumClass);
        this.enumClass = enumClass;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) throws DatabindException {
        Class<?> rawClass = context.getContextualType().getRawClass();
        if (rawClass.isEnum()) {
            return new CaseInsensitiveEnumDeserializer((Class<? extends Enum>) rawClass);
        }
        return this;
    }

    @Override
    public Enum<?> deserialize(JsonParser parser, DeserializationContext context) {
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
