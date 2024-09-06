package no.nav.skattekortservice.service;

import lombok.Builder;
import lombok.Data;
import no.nav.testnav.libs.dto.skattekortservice.v1.KodeverkTyper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class KodeverkService {
    public Map<String, String> getType(KodeverkTyper kodeverkstype) {

        return new TreeMap<>(
                Arrays.stream(kodeverkstype.getValue().getEnumConstants())
                        .map(Objects::toString)
                        .map(type -> type.split(","))
                        .map(splitType -> Mapping.builder()
                                .key(getValue(splitType[1]))
                                .value(splitType[0])
                                .build())
                        .collect(Collectors.toMap(Mapping::getKey, Mapping::getValue))
        );
    }

    private static String getValue(String rawValue) {

        var value = String.join(" ", StringUtils.splitByCharacterTypeCamelCase(rawValue));
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase()
                .replace("oe", "ø")
                .replace("aa", "å")
                .replace("ok", "OK")
                .replace("nav", "NAV")
                .replace("svalbard", "Svalbard");
    }

    @Data
    @Builder
    public static class Mapping {

        private String key;
        private String value;
    }
}
