package no.nav.registre.udistub.core.converter.ws;

import no.nav.registre.udistub.core.service.to.UdiPeriode;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class PeriodeWsConverter implements Converter<UdiPeriode, no.udi.mt_1067_nav_data.v1.Periode> {

	private final ConversionService conversionService;

	public PeriodeWsConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public no.udi.mt_1067_nav_data.v1.Periode convert(UdiPeriode periode) {
		if (periode != null) {
			no.udi.mt_1067_nav_data.v1.Periode resultatPeriode = new no.udi.mt_1067_nav_data.v1.Periode();
			resultatPeriode.setFra(conversionService.convert(periode.getFra(), XMLGregorianCalendar.class));
			resultatPeriode.setTil(conversionService.convert(periode.getTil(), XMLGregorianCalendar.class));
			return resultatPeriode;
		}
		return null;
	}
}