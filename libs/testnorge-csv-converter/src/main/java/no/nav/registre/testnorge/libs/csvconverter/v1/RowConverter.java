package no.nav.registre.testnorge.libs.csvconverter.v1;

import java.util.Map;

@FunctionalInterface
public interface RowConverter<T> {
    /**
     * map key = header and value = cell
     */
    T convert(Map<String, Object> rowMap);
}