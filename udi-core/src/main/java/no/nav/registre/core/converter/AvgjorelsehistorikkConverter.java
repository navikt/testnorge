package no.nav.registre.core.converter;

import no.nav.registre.core.database.model.Person;
import no.nav.registre.core.database.model.PersonAvgjorelse;
import no.udi.mt_1067_nav_data.v1.Avgjorelse;
import no.udi.mt_1067_nav_data.v1.AvgjorelseListe;
import no.udi.mt_1067_nav_data.v1.Avgjorelser;
import no.udi.mt_1067_nav_data.v1.Avgjorelsestype;
import no.udi.mt_1067_nav_data.v1.Periode;
import no.udi.mt_1067_nav_data.v1.Tillatelse;
import no.udi.mt_1067_nav_data.v1.Utfall;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class AvgjorelsehistorikkConverter implements Converter<Person, Avgjorelser> {

	private final ConversionService conversionService;

	public AvgjorelsehistorikkConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public Avgjorelser convert(Person person) {
		if (person != null) {
			Avgjorelser avgjorelser = new Avgjorelser();
			avgjorelser.setUavklart(person.isAvgjoerelseUavklart());
			avgjorelser.setAvgjorelseListe(new AvgjorelseListe());
			person.getAvgjoerelser().stream()
					.map(this::converterPersonAvgjorelse)
					.forEach(avgjorelser.getAvgjorelseListe().getAvgjorelse()::add);
			return avgjorelser;
		}
		return null;
	}

	private Avgjorelse converterPersonAvgjorelse(PersonAvgjorelse avgjorelse) {

		Avgjorelse udiAvgjorelse = new Avgjorelse();
		udiAvgjorelse.setAvgjorelseDato(conversionService.convert(avgjorelse.getAvgjoerelsesDato(), XMLGregorianCalendar.class));

		udiAvgjorelse.setAvgjorelseId(avgjorelse.getId().toString());

		Avgjorelsestype avgjorelsestype = new Avgjorelsestype();
		avgjorelsestype.setGrunntypeKode(avgjorelse.getGrunntypeKode());
		avgjorelsestype.setTillatelseKode(avgjorelse.getTillatelseKode());
		avgjorelsestype.setUtfallstypeKode(avgjorelse.getUtfallstypeKode());
		udiAvgjorelse.setAvgjorelsestype(avgjorelsestype);

		udiAvgjorelse.setEffektueringsDato(conversionService.convert(avgjorelse.getEffektueringsDato(), XMLGregorianCalendar.class));
		udiAvgjorelse.setErPositiv(avgjorelse.isErPositiv());
		udiAvgjorelse.setEtat(avgjorelse.getEtat());
		udiAvgjorelse.setFlyktingstatus(avgjorelse.isFlyktningstatus());
		udiAvgjorelse.setIverksettelseDato(conversionService.convert(avgjorelse.getIverksettelseDato(), XMLGregorianCalendar.class));

		udiAvgjorelse.setOmgjortavAvgjorelseId(avgjorelse.getOmgjortAvgjoerelsesId());
		udiAvgjorelse.setSaksnummer(avgjorelse.getSaksnummer().toString());

		Tillatelse tillatelse = new Tillatelse();
		tillatelse.setGyldighetsperiode(conversionService.convert(avgjorelse.getTillatelsePeriode(), Periode.class));
		tillatelse.setVarighet(avgjorelse.getTillatelseVarighet());
		tillatelse.setVarighetKode(avgjorelse.getTillatelseVarighetKode());
		udiAvgjorelse.setTillatelse(tillatelse);

		udiAvgjorelse.setUavklartFlyktningstatus(avgjorelse.isUavklartFlyktningstatus());

		Utfall utfall = new Utfall();
		utfall.setGjeldendePeriode(conversionService.convert(avgjorelse.getTillatelsePeriode(), Periode.class));
		utfall.setVarighet(avgjorelse.getTillatelseVarighet());
		utfall.setVarighetKode(avgjorelse.getTillatelseVarighetKode());
		udiAvgjorelse.setUtfall(utfall);

		if (avgjorelse.getUtreisefristDato() != null) {
			udiAvgjorelse.setUtreisefristDato(conversionService.convert(avgjorelse.getIverksettelseDato(), XMLGregorianCalendar.class));
		} else {
			udiAvgjorelse.setUtreisefristDato(null);
		}
		return udiAvgjorelse;

	}
}
