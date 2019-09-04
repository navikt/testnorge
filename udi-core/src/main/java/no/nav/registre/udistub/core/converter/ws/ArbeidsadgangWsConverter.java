package no.nav.registre.udistub.core.converter.ws;

import no.nav.registre.udistub.core.service.to.UdiArbeidsadgang;
import no.nav.registre.udistub.core.service.to.UdiPerson;
import no.udi.mt_1067_nav_data.v1.Arbeidsadgang;
import no.udi.mt_1067_nav_data.v1.Periode;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArbeidsadgangWsConverter implements Converter<UdiPerson, Arbeidsadgang> {

    private final ConversionService conversionService;

    public ArbeidsadgangWsConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Arbeidsadgang convert(UdiPerson person) {
        if (person != null) {
            Arbeidsadgang resultArbeidsadgang = new no.udi.mt_1067_nav_data.v1.Arbeidsadgang();
            UdiArbeidsadgang arbeidsadgang = person.getArbeidsadgang();

            resultArbeidsadgang.setArbeidsadgangsPeriode(conversionService.convert(arbeidsadgang.getPeriode(), Periode.class));
            resultArbeidsadgang.setArbeidsOmfang(arbeidsadgang.getArbeidsOmfang());
            resultArbeidsadgang.setHarArbeidsadgang(arbeidsadgang.getHarArbeidsAdgang());
            resultArbeidsadgang.setTypeArbeidsadgang(arbeidsadgang.getTypeArbeidsadgang());

            return resultArbeidsadgang;
        }
        return null;
    }
}
