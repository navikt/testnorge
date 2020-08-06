package no.nav.dolly.bestilling.skjermingsregister.mapper;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;

@Slf4j
@Component
public class SkjermingsRegisterMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, SkjermingsDataRequest.class)
                .customize(new CustomMapper<Person, SkjermingsDataRequest>() {
                    @Override
                    public void mapAtoB(Person person, SkjermingsDataRequest request, MappingContext context) {

                        request.setFornavn(person.getFornavn());
                        request.setEtternavn(person.getEtternavn());
                        request.setPersonident(person.getIdent());
                        request.setSkjermetFra(person.getEgenAnsattDatoFom());
                        request.setSkjermetTil(person.getEgenAnsattDatoTom());
                    }
                })
                .byDefault()
                .register();
    }
}
