package no.nav.registre.sdForvalter.converter.csv;

import java.util.Map;

@FunctionalInterface
interface ObjectConverter<T> {
    Map<String, Object> convert(T object);
}
