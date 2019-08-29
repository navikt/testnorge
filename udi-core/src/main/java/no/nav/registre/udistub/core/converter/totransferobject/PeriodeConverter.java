package no.nav.registre.udistub.core.converter.totransferobject;

import lombok.NoArgsConstructor;
import no.nav.registre.udistub.core.database.model.Periode;
import no.nav.registre.udistub.core.service.to.PeriodeTo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class PeriodeConverter implements Converter<Periode, PeriodeTo> {

    @Override
    public PeriodeTo convert(Periode periode) {
        if (periode != null) {
            return PeriodeTo.builder()
                    .fra(periode.getFra())
                    .til(periode.getTil())
                    .build();
        }
        return null;
    }
}