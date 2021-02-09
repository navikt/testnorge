package no.nav.udistub.converter.ws;

import no.nav.udistub.service.dto.UdiPeriode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class PeriodeWsConverter implements Converter<UdiPeriode, no.udi.mt_1067_nav_data.v1.Periode> {

	@Override
	public no.udi.mt_1067_nav_data.v1.Periode convert(UdiPeriode periode) {

		if (nonNull(periode)) {
            XmlDateWsConverter xmlDateWsConverter = new XmlDateWsConverter();
			no.udi.mt_1067_nav_data.v1.Periode resultatPeriode = new no.udi.mt_1067_nav_data.v1.Periode();
            resultatPeriode.setFra(xmlDateWsConverter.convert(periode.getFra()));
            resultatPeriode.setTil(xmlDateWsConverter.convert(periode.getTil()));
			return resultatPeriode;
		}
		return null;
	}
}