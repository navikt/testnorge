package no.nav.udistub.converter.ws;

import no.nav.udistub.service.dto.UdiArbeidsadgang;
import no.udi.mt_1067_nav_data.v1.ArbeidsadgangUtvidet;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

@Component
public class ArbeidsadgangUtvidetWsConverter implements Converter<UdiArbeidsadgang, ArbeidsadgangUtvidet> {

    @Override
    public ArbeidsadgangUtvidet convert(UdiArbeidsadgang arbeidsadgangUtvidet) {

        PeriodeWsConverter periodeWsConverter = new PeriodeWsConverter();

        var resultArbeidsadgang = new ArbeidsadgangUtvidet();

        resultArbeidsadgang.setArbeidsadgangsPeriode(periodeWsConverter.convert(arbeidsadgangUtvidet.getPeriode()));
        resultArbeidsadgang.setArbeidsOmfang(arbeidsadgangUtvidet.getArbeidsOmfang());
        JaNeiUavklart harArbeidsadgang = arbeidsadgangUtvidet.getHarArbeidsAdgang();
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
        resultArbeidsadgang.setTypeArbeidsadgang(arbeidsadgangUtvidet.getTypeArbeidsadgang());
        resultArbeidsadgang.setForklaring(arbeidsadgangUtvidet.getForklaring());
        resultArbeidsadgang.setHjemmel(arbeidsadgangUtvidet.getHjemmel());

        return resultArbeidsadgang;
    }
}
