package no.nav.registre.testnorge.libs.csvconverter;

import java.util.Map;

@FunctionalInterface
public interface ObjectConverter<T> {
    Map<String, Object> convert(T object);
}
