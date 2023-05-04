package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class PersonMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(DbRelasjon.class, FullPersonDTO.RelasjonDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(DbRelasjon kilde, FullPersonDTO.RelasjonDTO destinasjon, MappingContext context) {

                        destinasjon.setRelatertPerson(nonNull(kilde.getRelatertPerson()) ?
                                kilde.getRelatertPerson().getPerson() : null);
                    }
                })
                .byDefault()
                .register();
    }
}