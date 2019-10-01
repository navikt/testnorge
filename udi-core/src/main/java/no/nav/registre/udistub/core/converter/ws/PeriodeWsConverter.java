package no.nav.registre.udistub.core.converter.ws;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.nav.registre.udistub.core.service.to.UdiPeriode;

@Component
public class PeriodeWsConverter implements Converter<UdiPeriode, no.udi.mt_1067_nav_data.v1.Periode> {

	@Override
	public no.udi.mt_1067_nav_data.v1.Periode convert(UdiPeriode periode) {
		if (periode != null) {

            XmlDateWsConverter xmlDateWsConverter = new XmlDateWsConverter();
			no.udi.mt_1067_nav_data.v1.Periode resultatPeriode = new no.udi.mt_1067_nav_data.v1.Periode();
            resultatPeriode.setFra(xmlDateWsConverter.convert(periode.getFra()));
            resultatPeriode.setTil(xmlDateWsConverter.convert(periode.getTil()));
			return resultatPeriode;
		}
		return null;
	}
}