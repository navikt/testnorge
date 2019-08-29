package no.nav.registre.udistub.core.converter.ws;

import no.nav.registre.udistub.core.service.to.AvgjorelseTo;
import no.nav.registre.udistub.core.service.to.PersonTo;
import no.udi.mt_1067_nav_data.v1.Arbeidsadgang;
import no.udi.mt_1067_nav_data.v1.Avgjorelser;
import no.udi.mt_1067_nav_data.v1.GjeldendePerson;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import no.udi.mt_1067_nav_data.v1.SoknadOmBeskyttelseUnderBehandling;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Date;
import java.time.Instant;

@Component
public class PersonWsConverter implements Converter<PersonTo, HentPersonstatusResultat> {

	private final ConversionService conversionService;

	public PersonWsConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public HentPersonstatusResultat convert(PersonTo person) {
		if (person != null) {
			HentPersonstatusResultat hentPersonstatusResultat = new HentPersonstatusResultat();
			hentPersonstatusResultat.setGjeldendePerson(conversionService.convert(person, GjeldendePerson.class));
			hentPersonstatusResultat.setArbeidsadgang(conversionService.convert(person, Arbeidsadgang.class));
			hentPersonstatusResultat.setAvgjorelsehistorikk(conversionService.convert(person, Avgjorelser.class));
			hentPersonstatusResultat.setForesporselsfodselsnummer(person.getIdent());
			hentPersonstatusResultat.setHarFlyktningstatus(person.getFlyktning());

			hentPersonstatusResultat.setHistorikkHarFlyktningstatus(person.getAvgjoerelser()
					.stream()
					.anyMatch(AvgjorelseTo::getHarFlyktningstatus));

			hentPersonstatusResultat.setUavklartFlyktningstatus(person.getAvgjoerelseUavklart());
			hentPersonstatusResultat.setSoknadOmBeskyttelseUnderBehandling(conversionService.convert(person, SoknadOmBeskyttelseUnderBehandling.class));

			hentPersonstatusResultat.setUttrekkstidspunkt(conversionService.convert(new Date(Instant.now().getEpochSecond()), XMLGregorianCalendar.class));

			return hentPersonstatusResultat;
		}
		return null;
	}
}