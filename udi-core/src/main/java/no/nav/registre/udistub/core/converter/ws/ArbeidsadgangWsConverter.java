package no.nav.registre.udistub.core.converter.ws;

import no.udi.mt_1067_nav_data.v1.Arbeidsadgang;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.nav.registre.udistub.core.service.to.UdiArbeidsadgang;

@Component
public class ArbeidsadgangWsConverter implements Converter<UdiArbeidsadgang, Arbeidsadgang> {

    @Override
    public Arbeidsadgang convert(UdiArbeidsadgang arbeidsadgang) {
        if (arbeidsadgang != null) {

            PeriodeWsConverter periodeWsConverter = new PeriodeWsConverter();

            Arbeidsadgang resultArbeidsadgang = new Arbeidsadgang();

            resultArbeidsadgang.setArbeidsadgangsPeriode(periodeWsConverter.convert(arbeidsadgang.getPeriode()));
            resultArbeidsadgang.setArbeidsOmfang(arbeidsadgang.getArbeidsOmfang());
            resultArbeidsadgang.setHarArbeidsadgang(arbeidsadgang.getHarArbeidsadgang());
            resultArbeidsadgang.setTypeArbeidsadgang(arbeidsadgang.getTypeArbeidsadgang());

            return resultArbeidsadgang;
        }
        return null;
    }
}
