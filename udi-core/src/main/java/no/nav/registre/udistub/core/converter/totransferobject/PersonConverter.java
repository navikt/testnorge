package no.nav.registre.udistub.core.converter.totransferobject;

import no.nav.registre.udistub.core.database.model.Person;
import no.nav.registre.udistub.core.service.to.AliasTo;
import no.nav.registre.udistub.core.service.to.ArbeidsadgangTo;
import no.nav.registre.udistub.core.service.to.AvgjorelseTo;
import no.nav.registre.udistub.core.service.to.PersonTo;
import no.nav.registre.udistub.core.service.to.opphold.OppholdStatusTo;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PersonConverter implements Converter<Person, PersonTo> {

    private final ConversionService conversionService;

    public PersonConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public PersonTo convert(Person person) {
        if (person != null) {

            return PersonTo.builder()
                    .avgjoerelser(person.getAvgjoerelser().stream()
                            .map(avgjorelse -> conversionService.convert(avgjorelse, AvgjorelseTo.class))
                            .collect(Collectors.toList()))
                    .aliaser(person.getAliaser().stream()
                            .map(alias -> conversionService.convert(alias, AliasTo.class))
                            .collect(Collectors.toList()))
                    .arbeidsadgang(conversionService.convert(person.getArbeidsadgang(), ArbeidsadgangTo.class))
                    .oppholdStatus(conversionService.convert(person.getOppholdStatus(), OppholdStatusTo.class))
                    .avgjoerelseUavklart(person.getAvgjoerelseUavklart())
                    .harOppholdsTillatelse(person.getHarOppholdsTillatelse())
                    .flyktning(person.getFlyktning())
                    .build();
        }
        return null;
    }
}