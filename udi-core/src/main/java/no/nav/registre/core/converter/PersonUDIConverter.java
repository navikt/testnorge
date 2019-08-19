package no.nav.registre.core.converter;

import no.nav.registre.core.database.model.Person;
import no.nav.registre.core.database.model.Avgjorelse;
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
public class PersonUDIConverter implements Converter<Person, HentPersonstatusResultat> {

	private final ConversionService conversionService;

	public PersonUDIConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public HentPersonstatusResultat convert(Person person) {
		if (person != null) {
			HentPersonstatusResultat hentPersonstatusResultat = new HentPersonstatusResultat();
			hentPersonstatusResultat.setGjeldendePerson(conversionService.convert(person, GjeldendePerson.class));
			hentPersonstatusResultat.setArbeidsadgang(conversionService.convert(person, Arbeidsadgang.class));
			hentPersonstatusResultat.setAvgjorelsehistorikk(conversionService.convert(person, Avgjorelser.class));
			hentPersonstatusResultat.setForesporselsfodselsnummer(person.getFnr());
			hentPersonstatusResultat.setHarFlyktningstatus(person.getFlyktning());

			hentPersonstatusResultat.setHistorikkHarFlyktningstatus(person.getAvgjoerelser()
					.stream()
					.anyMatch(Avgjorelse::isFlyktningstatus));

			hentPersonstatusResultat.setUavklartFlyktningstatus(person.isAvgjoerelseUavklart());
			hentPersonstatusResultat.setSoknadOmBeskyttelseUnderBehandling(conversionService.convert(person, SoknadOmBeskyttelseUnderBehandling.class));

			hentPersonstatusResultat.setUttrekkstidspunkt(conversionService.convert(new Date(Instant.now().getEpochSecond()), XMLGregorianCalendar.class));

			return hentPersonstatusResultat;
		}
		return null;
	}
}