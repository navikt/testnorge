package no.nav.registre.core.converter.udi;

import no.nav.registre.core.converter.BaseConverter;
import no.nav.registre.core.database.model.Person;
import no.udi.mt_1067_nav_data.v1.SoknadOmBeskyttelseUnderBehandling;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class BeskyttleseUnderBehandlingConverter extends BaseConverter<Person, SoknadOmBeskyttelseUnderBehandling> {

    private final ConversionService conversionService;

    BeskyttleseUnderBehandlingConverter(ConversionService conversionService) {
        this.conversionService = this.registerConverter(conversionService);
    }

    @Override
    public SoknadOmBeskyttelseUnderBehandling convert(Person person) {
        SoknadOmBeskyttelseUnderBehandling beskyttelseUnderBehandling = new SoknadOmBeskyttelseUnderBehandling();
        beskyttelseUnderBehandling.setErUnderBehandling(person.getSoeknadOmBeskyttelseUnderBehandling());
        beskyttelseUnderBehandling.setSoknadsdato(conversionService.convert(person.getSoknadDato(), XMLGregorianCalendar.class));
        return beskyttelseUnderBehandling;
    }
}
