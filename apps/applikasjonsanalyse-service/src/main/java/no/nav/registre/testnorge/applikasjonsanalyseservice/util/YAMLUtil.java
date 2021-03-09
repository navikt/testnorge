package no.nav.registre.testnorge.applikasjonsanalyseservice.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

public class YAMLUtil {

    private static YAMLUtil instance;
    private final ObjectMapper objectMapper;

    private YAMLUtil() {
        objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.findAndRegisterModules();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static YAMLUtil Instance() {
        if (instance == null) {
            instance = new YAMLUtil();
        }
        return instance;
    }

    public <T> T read(String value, Class<T> clazz) throws IOException {
        return objectMapper.readValue(value, clazz);
    }
}
