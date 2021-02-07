package no.nav.udistub.service.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.udistub.converter.MappingStrategy;
import no.nav.udistub.database.model.Person;
import no.nav.udistub.service.dto.UdiPerson;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class PersonMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(UdiPerson.class, Person.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(UdiPerson udiPerson, Person person, MappingContext context) {

                        person.getAliaser().forEach(alias -> alias.setPerson(person));
                        person.setAvgjoerelser(null); //TODO Teknisk gjeld avgjørelser
//                        person.getAvgjoerelser().forEach(avgjoerlse -> avgjoerlse.setPerson(person));
                        if (nonNull(person.getArbeidsadgang())) {
                            person.getArbeidsadgang().setPerson(person);
                        }
                        if (nonNull(person.getArbeidsadgangUtvidet())) {
                            person.getArbeidsadgangUtvidet().setPerson(person);
                        }
                        if (nonNull(person.getOppholdStatus())) {
                            person.getOppholdStatus().setPerson(person);
                        }
                    }
                })
                .byDefault()
                .register();
    }
}