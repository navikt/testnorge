package no.nav.testnav.libs.csvconverter;

import java.util.Map;

@FunctionalInterface
public interface RowConverter<T> {
    /**
     * map key = header and value = cell
     */
    T convert(Map<String, Object> rowMap);
}