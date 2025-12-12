package no.nav.udistub.converter.ws;

import no.nav.udistub.service.dto.UdiArbeidsadgang;
import no.udi.mt_1067_nav_data.v1.Arbeidsadgang;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

@Component
public class ArbeidsadgangWsConverter implements Converter<UdiArbeidsadgang, Arbeidsadgang> {

    @Override
    public Arbeidsadgang convert(UdiArbeidsadgang arbeidsadgang) {

        PeriodeWsConverter periodeWsConverter = new PeriodeWsConverter();
        Arbeidsadgang resultArbeidsadgang = new Arbeidsadgang();

        resultArbeidsadgang.setArbeidsadgangsPeriode(periodeWsConverter.convert(arbeidsadgang.getPeriode()));
        resultArbeidsadgang.setArbeidsOmfang(arbeidsadgang.getArbeidsOmfang());
        JaNeiUavklart harArbeidsadgang = arbeidsadgang.getHarArbeidsAdgang();
        LocalDate now = LocalDate.now();
        if (harArbeidsadgang == null) {
            java.util.Date til = resultArbeidsadgang.getArbeidsadgangsPeriode().getTil().toGregorianCalendar().getTime();
            java.util.Date fra = resultArbeidsadgang.getArbeidsadgangsPeriode().getFra().toGregorianCalendar().getTime();
            if (fra.before(Date.valueOf(now)) && til.after(Date.valueOf(now))) {
                harArbeidsadgang = JaNeiUavklart.JA;
            } else if (Date.valueOf(now).before(fra) || Date.valueOf(now).after(til)) {
                harArbeidsadgang = JaNeiUavklart.NEI;
            } else {
                harArbeidsadgang = JaNeiUavklart.UAVKLART;
            }
        }
        resultArbeidsadgang.setHarArbeidsadgang(harArbeidsadgang);
        resultArbeidsadgang.setTypeArbeidsadgang(arbeidsadgang.getTypeArbeidsadgang());

        return resultArbeidsadgang;
    }
}
