package no.nav.registre.udistub.core.converter.totransferobject;

import lombok.NoArgsConstructor;
import no.nav.registre.udistub.core.database.model.opphold.UtvistMedInnreiseForbud;
import no.nav.registre.udistub.core.service.to.opphold.UtvistMedInnreiseForbudTo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class UtvistMedInnreiseForbudConverter implements Converter<UtvistMedInnreiseForbud, UtvistMedInnreiseForbudTo> {

    @Override
    public UtvistMedInnreiseForbudTo convert(UtvistMedInnreiseForbud utvistMedInnreiseForbud) {
        if (utvistMedInnreiseForbud != null) {
            return UtvistMedInnreiseForbudTo.builder()
                    .innreiseForbud(utvistMedInnreiseForbud.getInnreiseForbud())
                    .innreiseForbudVedtaksDato(utvistMedInnreiseForbud.getInnreiseForbudVedtaksDato())
                    .varighet(utvistMedInnreiseForbud.getVarighet())
                    .build();
        }
        return null;
    }
}
