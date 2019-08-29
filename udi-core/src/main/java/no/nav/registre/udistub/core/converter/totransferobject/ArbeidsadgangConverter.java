package no.nav.registre.udistub.core.converter.totransferobject;

import no.nav.registre.udistub.core.database.model.Arbeidsadgang;
import no.nav.registre.udistub.core.service.to.ArbeidsadgangTo;
import no.nav.registre.udistub.core.service.to.PeriodeTo;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArbeidsadgangConverter implements Converter<Arbeidsadgang, ArbeidsadgangTo> {

    private final ConversionService conversionService;

    public ArbeidsadgangConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public ArbeidsadgangTo convert(Arbeidsadgang arbeidsadgang) {
        if (arbeidsadgang != null) {
            return ArbeidsadgangTo.builder()
                    .harArbeidsAdgang(arbeidsadgang.getHarArbeidsAdgang())
                    .typeArbeidsadgang(arbeidsadgang.getTypeArbeidsadgang())
                    .arbeidsOmfang(arbeidsadgang.getArbeidsOmfang())
                    .periode(conversionService.convert(arbeidsadgang.getPeriode(), PeriodeTo.class))
                    .build();
        }
        return null;
    }
}
