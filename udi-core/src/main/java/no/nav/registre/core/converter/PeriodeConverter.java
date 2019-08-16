package no.nav.registre.core.converter;

import no.nav.registre.core.database.model.Periode;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class PeriodeConverter implements Converter<Periode, no.udi.mt_1067_nav_data.v1.Periode> {

	private final ConversionService conversionService;

	public PeriodeConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public no.udi.mt_1067_nav_data.v1.Periode convert(Periode periode) {
		if (periode != null) {
			no.udi.mt_1067_nav_data.v1.Periode resultatPeriode = new no.udi.mt_1067_nav_data.v1.Periode();
			resultatPeriode.setFra(conversionService.convert(periode.getFra(), XMLGregorianCalendar.class));
			resultatPeriode.setTil(conversionService.convert(periode.getTil(), XMLGregorianCalendar.class));
			return resultatPeriode;
		}
		return null;
	}
}