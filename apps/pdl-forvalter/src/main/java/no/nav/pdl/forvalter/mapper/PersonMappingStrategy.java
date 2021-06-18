package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.domain.FullPersonDTO;
import org.springframework.stereotype.Component;

@Component
public class PersonMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(DbPerson.class, FullPersonDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(DbPerson kilde, FullPersonDTO destinasjon, MappingContext context) {

                    }
                })
                .byDefault()
                .register();

        factory.classMap(DbRelasjon.class, FullPersonDTO.RelasjonDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(DbRelasjon kilde, FullPersonDTO.RelasjonDTO destinasjon, MappingContext context) {

                        destinasjon.setRelatertPerson(kilde.getRelatertPerson().getPerson());
                    }
                })
                .byDefault()
                .register();
    }
}