package no.nav.registre.core.converter;

import no.nav.registre.core.database.model.Person;
import no.udi.mt_1067_nav_data.v1.Arbeidsadgang;
import no.udi.mt_1067_nav_data.v1.Periode;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArbeidsadgangConverter implements Converter<Person, Arbeidsadgang> {

	private final ConversionService conversionService;

	public ArbeidsadgangConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public Arbeidsadgang convert(Person person) {
		if (person != null) {
			no.udi.mt_1067_nav_data.v1.Arbeidsadgang resultArbeidsadgang = new no.udi.mt_1067_nav_data.v1.Arbeidsadgang();
			no.nav.registre.core.database.model.Arbeidsadgang arbeidsadgang = person.getArbeidsadgang();

			resultArbeidsadgang.setArbeidsadgangsPeriode(conversionService.convert(arbeidsadgang.getPeriode(), Periode.class));
			resultArbeidsadgang.setArbeidsOmfang(arbeidsadgang.getArbeidsOmfang());
			resultArbeidsadgang.setHarArbeidsadgang(arbeidsadgang.getHarArbeidsAdgang());
			resultArbeidsadgang.setTypeArbeidsadgang(arbeidsadgang.getTypeArbeidsadgang());

			return resultArbeidsadgang;
		}
		return null;
	}
}
