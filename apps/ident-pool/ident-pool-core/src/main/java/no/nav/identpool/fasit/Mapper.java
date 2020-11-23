package no.nav.identpool.fasit;

public interface Mapper {

    boolean supported(Class<?> clazz);

    <T> T map(String json, Class<T> type);
}