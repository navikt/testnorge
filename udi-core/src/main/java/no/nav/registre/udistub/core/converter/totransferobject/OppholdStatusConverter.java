package no.nav.registre.udistub.core.converter.totransferobject;

import no.nav.registre.udistub.core.database.model.opphold.OppholdStatus;
import no.nav.registre.udistub.core.service.to.PeriodeTo;
import no.nav.registre.udistub.core.service.to.opphold.IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo;
import no.nav.registre.udistub.core.service.to.opphold.OppholdSammeVilkaarTo;
import no.nav.registre.udistub.core.service.to.opphold.OppholdStatusTo;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OppholdStatusConverter implements Converter<OppholdStatus, OppholdStatusTo> {

    private final ConversionService conversionService;

    public OppholdStatusConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public OppholdStatusTo convert(OppholdStatus oppholdStatus) {
        if (oppholdStatus != null) {
            return OppholdStatusTo.builder()
                    .uavklart(oppholdStatus.getUavklart())
                    .eosEllerEFTABeslutningOmOppholdsrett(oppholdStatus.getEosEllerEFTABeslutningOmOppholdsrett())
                    .eosEllerEFTABeslutningOmOppholdsrettPeriode(conversionService.convert(oppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode(), PeriodeTo.class))
                    .eosEllerEFTABeslutningOmOppholdsrettEffektuering(oppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettEffektuering())

                    .eosEllerEFTAOppholdstillatelse(oppholdStatus.getEosEllerEFTAOppholdstillatelse())
                    .eosEllerEFTAOppholdstillatelsePeriode(conversionService.convert(oppholdStatus.getEosEllerEFTAOppholdstillatelsePeriode(), PeriodeTo.class))
                    .eosEllerEFTAOppholdstillatelseEffektuering(oppholdStatus.getEosEllerEFTAOppholdstillatelseEffektuering())

                    .eosEllerEFTAVedtakOmVarigOppholdsrett(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrett())
                    .eosEllerEFTAVedtakOmVarigOppholdsrettPeriode(conversionService.convert(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode(), PeriodeTo.class))
                    .eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering(oppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettEffektuering())

                    .eosEllerEFTAOppholdstillatelse(oppholdStatus.getEosEllerEFTAOppholdstillatelse())
                    .oppholdSammeVilkaar(conversionService.convert(oppholdStatus.getOppholdSammeVilkaar(), OppholdSammeVilkaarTo.class))
                    .ikkeOppholdstilatelseIkkeVilkaarIkkeVisum(conversionService.convert(oppholdStatus.getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum(), IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo.class))
                    .build();
        }
        return null;
    }
}

