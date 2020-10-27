package no.nav.registre.testnorge.identservice.testdata.mapping;

import ma.glasnost.orika.MapperFactory;

@FunctionalInterface
public interface MappingStrategy {

    void register(MapperFactory factory);

}
