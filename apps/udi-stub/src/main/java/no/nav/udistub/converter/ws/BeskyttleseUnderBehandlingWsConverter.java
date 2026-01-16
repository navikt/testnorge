package no.nav.udistub.converter.ws;

import no.nav.udistub.service.dto.UdiPerson;
import no.udi.mt_1067_nav_data.v1.SoknadOmBeskyttelseUnderBehandling;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class BeskyttleseUnderBehandlingWsConverter implements Converter<UdiPerson, SoknadOmBeskyttelseUnderBehandling> {

    @Override
    public SoknadOmBeskyttelseUnderBehandling convert(UdiPerson person) {
        if (isNull(person)) {
            return null;
        }

        XmlDateWsConverter xmlDateWsConverter = new XmlDateWsConverter();
        SoknadOmBeskyttelseUnderBehandling beskyttelseUnderBehandling = new SoknadOmBeskyttelseUnderBehandling();
        beskyttelseUnderBehandling.setErUnderBehandling(person.getSoeknadOmBeskyttelseUnderBehandling());
        beskyttelseUnderBehandling.setSoknadsdato(xmlDateWsConverter.convert(person.getSoknadDato()));
        return beskyttelseUnderBehandling;
    }
}
