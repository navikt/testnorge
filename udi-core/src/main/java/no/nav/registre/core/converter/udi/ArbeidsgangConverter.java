package no.nav.registre.core.converter.udi;

import no.nav.registre.core.converter.BaseConverter;
import no.nav.registre.core.database.model.Person;
import no.udi.mt_1067_nav_data.v1.Arbeidsadgang;
import no.udi.mt_1067_nav_data.v1.Periode;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class ArbeidsgangConverter extends BaseConverter<Person, Arbeidsadgang> {

    private final ConversionService conversionService;

    public ArbeidsgangConverter(ConversionService conversionService) {
        this.conversionService = this.registerConverter(conversionService);
    }

    @Override
    public Arbeidsadgang convert(Person person) {
        no.udi.mt_1067_nav_data.v1.Arbeidsadgang resultArbeidsadgang = new no.udi.mt_1067_nav_data.v1.Arbeidsadgang();
        no.nav.registre.core.database.model.Arbeidsadgang arbeidsadgang = person.getArbeidsadgang();

        resultArbeidsadgang.setArbeidsadgangsPeriode(conversionService.convert(arbeidsadgang.getPeriode(), Periode.class));
        resultArbeidsadgang.setArbeidsOmfang(arbeidsadgang.getArbeidsOmfang());
        resultArbeidsadgang.setHarArbeidsadgang(arbeidsadgang.getHarArbeidsAdgang());
        resultArbeidsadgang.setTypeArbeidsadgang(arbeidsadgang.getTypeArbeidsadgang());

        return resultArbeidsadgang;
    }
}
