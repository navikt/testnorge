package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.dto.RsPerson;
import org.springframework.stereotype.Component;

@Component
public class PersonMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(DbPerson.class, RsPerson.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(DbPerson kilde, RsPerson destinasjon, MappingContext context) {

                    }
                })
                .byDefault()
                .register();

        factory.classMap(DbRelasjon.class, RsPerson.RsRelasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(DbRelasjon kilde, RsPerson.RsRelasjon destinasjon, MappingContext context) {

                        destinasjon.setRelatertPerson(kilde.getRelatertPerson().getPerson());
                    }
                })
                .byDefault()
                .register();
    }
}