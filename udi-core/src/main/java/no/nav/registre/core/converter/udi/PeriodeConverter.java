package no.nav.registre.core.converter.udi;

import no.nav.registre.core.converter.BaseConverter;
import no.nav.registre.core.database.model.Periode;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class PeriodeConverter extends BaseConverter<Periode, no.udi.mt_1067_nav_data.v1.Periode> {

    private final ConversionService conversionService;

    public PeriodeConverter(ConversionService conversionService) {
        this.conversionService = this.registerConverter(conversionService);
    }

    @Override
    public no.udi.mt_1067_nav_data.v1.Periode convert(Periode periode) {
        no.udi.mt_1067_nav_data.v1.Periode resultatPeriode = new no.udi.mt_1067_nav_data.v1.Periode();
        resultatPeriode.setFra(conversionService.convert(periode.getFra(), XMLGregorianCalendar.class));
        resultatPeriode.setTil(conversionService.convert(periode.getTil(), XMLGregorianCalendar.class));
        return resultatPeriode;
    }
}