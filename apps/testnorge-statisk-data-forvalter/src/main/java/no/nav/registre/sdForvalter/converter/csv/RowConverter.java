package no.nav.registre.sdForvalter.converter.csv;

import java.util.Map;

@FunctionalInterface
interface RowConverter<T> {
    /**
     * map key = header and value = cell
     */
    T convert(Map<String, Object> rowMap);
}