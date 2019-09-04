package no.nav.registre.udistub.core.converter.ws;

import no.nav.registre.udistub.core.service.to.UdiPerson;
import no.udi.mt_1067_nav_data.v1.SoknadOmBeskyttelseUnderBehandling;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class BeskyttleseUnderBehandlingWsConverter implements Converter<UdiPerson, SoknadOmBeskyttelseUnderBehandling> {

	private final ConversionService conversionService;

	BeskyttleseUnderBehandlingWsConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public SoknadOmBeskyttelseUnderBehandling convert(UdiPerson person) {
		if (person != null) {
			SoknadOmBeskyttelseUnderBehandling beskyttelseUnderBehandling = new SoknadOmBeskyttelseUnderBehandling();
			beskyttelseUnderBehandling.setErUnderBehandling(person.getSoeknadOmBeskyttelseUnderBehandling());
			beskyttelseUnderBehandling.setSoknadsdato(conversionService.convert(person.getSoknadDato(), XMLGregorianCalendar.class));
			return beskyttelseUnderBehandling;
		}
		return null;
	}
}
