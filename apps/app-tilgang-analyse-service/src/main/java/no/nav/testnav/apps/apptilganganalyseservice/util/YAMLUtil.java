package no.nav.testnav.apps.apptilganganalyseservice.util;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.dataformat.yaml.YAMLMapper;

public class YAMLUtil {

    private static YAMLUtil instance;
    private final YAMLMapper objectMapper;

    private YAMLUtil() {
        objectMapper = YAMLMapper.builder()
                .findAndAddModules()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    public <T> T read(String value, Class<T> clazz) {
        return objectMapper.readValue(value, clazz);
    }

    public static YAMLUtil Instance() {
        if (instance == null) {
            instance = new YAMLUtil();
        }
        return instance;
    }
}
