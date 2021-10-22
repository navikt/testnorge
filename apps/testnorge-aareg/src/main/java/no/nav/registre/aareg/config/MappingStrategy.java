package no.nav.registre.aareg.config;

import ma.glasnost.orika.MapperFactory;

@FunctionalInterface
public interface MappingStrategy {

    void register(MapperFactory factory);
}
