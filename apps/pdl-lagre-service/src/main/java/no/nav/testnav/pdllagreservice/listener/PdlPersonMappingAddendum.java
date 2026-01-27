package no.nav.testnav.pdllagreservice.listener;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@UtilityClass
public class PdlPersonMappingAddendum {

    public static Map<String, Object> appendSizeAttribute(Map<String, Object> dokument) {

        if (isNull(dokument.get("hentPerson"))) {
            return dokument;
        }

        ((Map<String, Object>) dokument.get("hentPerson"))
                .forEach((key, value) -> {

            if (value instanceof List<?> list) {

                list.forEach(item -> {
                    if (item instanceof Map egenskap) {
                        egenskap.put("size", list.size());
                    }
                });
            }
        });

        return dokument;
    }
}
