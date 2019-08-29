package no.nav.registre.udistub.core.converter.totransferobject;

import no.nav.registre.udistub.core.database.model.opphold.IkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
import no.nav.registre.udistub.core.service.to.opphold.AvslagEllerBortfallTo;
import no.nav.registre.udistub.core.service.to.opphold.IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo;
import no.nav.registre.udistub.core.service.to.opphold.UtvistMedInnreiseForbudTo;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IkkeOppholdstilatelseIkkeVilkaarIkkeVisumToConverter
        implements Converter<IkkeOppholdstilatelseIkkeVilkaarIkkeVisum, IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo> {

    private final ConversionService conversionService;

    IkkeOppholdstilatelseIkkeVilkaarIkkeVisumToConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo convert(IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        if (ikkeOppholdstilatelseIkkeVilkaarIkkeVisum != null) {
            return IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo.builder()
                    .utvistMedInnreiseForbud(conversionService.convert(ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.getUtvistMedInnreiseForbud(), UtvistMedInnreiseForbudTo.class))
                    .avslagEllerBortfall(conversionService.convert(ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.getAvslagEllerBortfall(), AvslagEllerBortfallTo.class))
                    .ovrigIkkeOppholdsKategoriArsak(ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.getOvrigIkkeOppholdsKategoriArsak())
                    .build();
        }
        return null;
    }
}
