package no.nav.testnav.pdllagreservice.listener;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@UtilityClass
public class PdlPersonMappingAddendum {

    /**
     * Appends a "size" attribute to each list-type attribute within the "hentPerson" map of the provided dokument.
     *
     * @param dokument The dokument map containing person data.
     * @return The modified dokument map with "size" attributes added.
     */
    public static Map<String, Object> appendSizeAttribute(Map<String, Object> dokument) {

        if (isNull(dokument.get("hentPerson"))) {
            return dokument;
        }

        ((Map<String, Object>) dokument.get("hentPerson"))
                .forEach((key, value) -> {

            if (value instanceof List<?> list) {

                list.forEach(item -> {
                    if (item instanceof Map<?, ?> ) {
                        val egenskap = (Map<String, Object>) item;
                        egenskap.put("size", list.size());
                    }
                });
            }
        });

        return dokument;
    }
}
