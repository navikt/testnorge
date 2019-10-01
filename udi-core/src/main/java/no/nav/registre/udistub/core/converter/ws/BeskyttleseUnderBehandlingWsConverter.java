package no.nav.registre.udistub.core.converter.ws;

import no.udi.mt_1067_nav_data.v1.SoknadOmBeskyttelseUnderBehandling;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.nav.registre.udistub.core.service.to.UdiPerson;

@Component
public class BeskyttleseUnderBehandlingWsConverter implements Converter<UdiPerson, SoknadOmBeskyttelseUnderBehandling> {
	@Override
	public SoknadOmBeskyttelseUnderBehandling convert(UdiPerson person) {
		if (person != null) {
            XmlDateWsConverter xmlDateWsConverter = new XmlDateWsConverter();
			SoknadOmBeskyttelseUnderBehandling beskyttelseUnderBehandling = new SoknadOmBeskyttelseUnderBehandling();
			beskyttelseUnderBehandling.setErUnderBehandling(person.getSoeknadOmBeskyttelseUnderBehandling());
            beskyttelseUnderBehandling.setSoknadsdato(xmlDateWsConverter.convert(person.getSoknadDato()));
			return beskyttelseUnderBehandling;
		}
		return null;
	}
}
