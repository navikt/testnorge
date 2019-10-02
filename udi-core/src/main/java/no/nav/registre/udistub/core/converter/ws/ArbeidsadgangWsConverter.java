package no.nav.registre.udistub.core.converter.ws;

import no.udi.mt_1067_nav_data.v1.Arbeidsadgang;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

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
            JaNeiUavklart harArbeidsadgang = arbeidsadgang.getHarArbeidsadgang();
            LocalDate now = LocalDate.now();
            if (harArbeidsadgang == null) {
                java.util.Date til = resultArbeidsadgang.getArbeidsadgangsPeriode().getTil().toGregorianCalendar().getTime();
                java.util.Date fra = resultArbeidsadgang.getArbeidsadgangsPeriode().getFra().toGregorianCalendar().getTime();
                if (fra.after(Date.valueOf(now)) && til.after(Date.valueOf(now))) {
                    harArbeidsadgang = JaNeiUavklart.JA;
                } else if (fra.before(Date.valueOf(now)) || til.after(Date.valueOf(now))) {
                    harArbeidsadgang = JaNeiUavklart.NEI;
                } else {
                    harArbeidsadgang = JaNeiUavklart.UAVKLART;
                }
            }
            resultArbeidsadgang.setHarArbeidsadgang(harArbeidsadgang);
            resultArbeidsadgang.setTypeArbeidsadgang(arbeidsadgang.getTypeArbeidsadgang());

            return resultArbeidsadgang;
        }
        return null;
    }
}
