package no.nav.registre.udistub.core.converter.totransferobject;

import no.nav.registre.udistub.core.database.model.Avgjorelse;
import no.nav.registre.udistub.core.service.to.AvgjorelseTo;
import no.nav.registre.udistub.core.service.to.PeriodeTo;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AvgjorelseConverter implements Converter<Avgjorelse, AvgjorelseTo> {

    private final ConversionService conversionService;

    public AvgjorelseConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public AvgjorelseTo convert(Avgjorelse avgjorelse) {
        if (avgjorelse != null) {
            return AvgjorelseTo.builder()
                    .omgjortAvgjoerelsesId(avgjorelse.getOmgjortAvgjoerelsesId())
                    .utfallstypeKode(avgjorelse.getUtfallstypeKode())
                    .erPositiv(avgjorelse.isErPositiv())
                    .grunntypeKode(avgjorelse.getGrunntypeKode())
                    .tillatelseKode(avgjorelse.getTillatelseKode())
                    .utfallPeriode(conversionService.convert(avgjorelse.getUtfallPeriode(), PeriodeTo.class))
                    .tillatelseVarighetKode(avgjorelse.getTillatelseVarighetKode())
                    .tillatelseVarighet(avgjorelse.getTillatelseVarighet())
                    .tillatelsePeriode(conversionService.convert(avgjorelse.getTillatelsePeriode(), PeriodeTo.class))
                    .effektueringsDato(avgjorelse.getEffektueringsDato())
                    .avgjoerelsesDato(avgjorelse.getAvgjoerelsesDato())
                    .iverksettelseDato(avgjorelse.getIverksettelseDato())
                    .utreisefristDato(avgjorelse.getUtreisefristDato())
                    .saksnummer(avgjorelse.getSaksnummer())
                    .etat(avgjorelse.getEtat())
                    .harFlyktningstatus(avgjorelse.isHarFlyktningstatus())
                    .uavklartFlyktningstatus(avgjorelse.isUavklartFlyktningstatus())
                    .build();
        }
        return null;
    }
}
