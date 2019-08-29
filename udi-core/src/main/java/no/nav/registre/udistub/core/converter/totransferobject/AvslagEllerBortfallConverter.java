package no.nav.registre.udistub.core.converter.totransferobject;

import lombok.NoArgsConstructor;
import no.nav.registre.udistub.core.database.model.opphold.AvslagEllerBortfall;
import no.nav.registre.udistub.core.service.to.opphold.AvslagEllerBortfallTo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class AvslagEllerBortfallConverter implements Converter<AvslagEllerBortfall, AvslagEllerBortfallTo> {

    @Override
    public AvslagEllerBortfallTo convert(AvslagEllerBortfall avslagEllerBortfall) {
        if (avslagEllerBortfall != null) {
            return AvslagEllerBortfallTo.builder()
                    .avgjorelsesDato(avslagEllerBortfall.getAvgjorelsesDato())
                    .bortfallAvPOellerBOSDato(avslagEllerBortfall.getBortfallAvPOellerBOSDato())
                    .tilbakeKallVirkningsDato(avslagEllerBortfall.getTilbakeKallVirkningsDato())
                    .tilbakeKallUtreiseFrist(avslagEllerBortfall.getTilbakeKallUtreiseFrist())
                    .avslagOppholdstillatelseBehandletUtreiseFrist(avslagEllerBortfall.getAvslagOppholdstillatelseBehandletUtreiseFrist())
                    .avslagGrunnlagOverig(avslagEllerBortfall.getAvslagGrunnlagOverig())
                    .avslagGrunnlagTillatelseGrunnlagEOS(avslagEllerBortfall.getAvslagGrunnlagTillatelseGrunnlagEOS())
                    .avslagOppholdstillatelseUtreiseFrist(avslagEllerBortfall.getAvslagOppholdstillatelseUtreiseFrist())
                    .avslagOppholdstillatelseBehandletGrunnlagOvrig(avslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagOvrig())
                    .avslagOppholdstillatelseBehandletGrunnlagEOS(avslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagEOS())
                    .avslagOppholdsrettBehandlet(avslagEllerBortfall.getAvslagOppholdsrettBehandlet())
                    .build();
        }
        return null;
    }
}