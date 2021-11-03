package no.nav.dolly.mapper;

import ma.glasnost.orika.MapperFactory;

@FunctionalInterface
public interface MappingStrategy {

    /**
     * A callback for registering criteria on the provided {@link MapperFactory}.
     * <p/>
     * <pre>{@code
     *
     * @Override public void register(MapperFactory factory) {
     * factory.registerMapper(arbeidsfordelingToRestArbeidsfordeling());
     * }
     * }</pre>
     */
    void register(MapperFactory factory);
}
