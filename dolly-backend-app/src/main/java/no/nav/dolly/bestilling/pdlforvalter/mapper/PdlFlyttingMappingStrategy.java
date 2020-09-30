package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlInnflytting;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlUtflytting;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlFlyttingMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlInnflytting.class)
                .customize(new CustomMapper<Person, PdlInnflytting>() {
                    @Override
                    public void mapAtoB(Person person, PdlInnflytting innflytting, MappingContext context) {

                        innflytting.setFraflyttingsland(person.getInnvandretUtvandret().get(0).getLandkode());
                        innflytting.setKilde(CONSUMER);
                    }
                })
                .register();

        factory.classMap(Person.class, PdlUtflytting.class)
                .customize(new CustomMapper<Person, PdlUtflytting>() {
                    @Override
                    public void mapAtoB(Person person, PdlUtflytting utflytting, MappingContext context) {

                        utflytting.setTilflyttingsland(person.getInnvandretUtvandret().get(0).getLandkode());
                        utflytting.setKilde(CONSUMER);
                    }
                })
                .register();
    }
}
