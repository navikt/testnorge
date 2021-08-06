package no.nav.testnav.libs.csvconverter;

import java.util.Map;

@FunctionalInterface
public interface ObjectConverter<T> {
    Map<String, Object> convert(T object);
}
