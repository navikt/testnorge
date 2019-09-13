package no.nav.dolly.bestilling.udistub;

import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import java.time.LocalDate;
import java.util.Collections;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.udistub.model.UdiPeriode;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.avgjoerelse.UdiAvgjorelse;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdSammeVilkaar;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdStatus;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdstillatelseType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UdiStubDefaultPersonUtil {

    public static void setPersonDefaultsIfUnspecified(UdiPerson udiPerson) {

        //OPPHOLDSSTATUS
        UdiOppholdStatus specifiedUdiOppholdStatus = nullcheckSetDefaultValue(udiPerson.getOppholdStatus(), new UdiOppholdStatus());

        udiPerson.setOppholdStatus(UdiOppholdStatus.builder()
                .uavklart(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getUavklart(), false))
                .udiOppholdSammeVilkaar(oppholdSammeVilkaarDefaultsIfUnspecified(specifiedUdiOppholdStatus))
                .build());

        // AVGJORELSER
        udiPerson.setAvgjoerelser(nullcheckSetDefaultValue(udiPerson.getAvgjoerelser(), Collections.singletonList(new UdiAvgjorelse())));
        udiPerson.setHarOppholdsTillatelse(nullcheckSetDefaultValue(udiPerson.getHarOppholdsTillatelse(), true));
        udiPerson.setSoknadDato(nullcheckSetDefaultValue(udiPerson.getSoknadDato(), LocalDate.now()));
        udiPerson.setAvgjoerelseUavklart(nullcheckSetDefaultValue(udiPerson.getAvgjoerelseUavklart(), false));
    }

    private static UdiOppholdSammeVilkaar oppholdSammeVilkaarDefaultsIfUnspecified(UdiOppholdStatus udiOppholdStatus) {
        UdiOppholdSammeVilkaar udiOppholdSammeVilkaar = nullcheckSetDefaultValue(udiOppholdStatus.getUdiOppholdSammeVilkaar(), new UdiOppholdSammeVilkaar());

        udiOppholdSammeVilkaar.setOppholdSammeVilkaarEffektuering(nullcheckSetDefaultValue(udiOppholdSammeVilkaar.getOppholdstillatelseVedtaksDato(), LocalDate.now()));
        udiOppholdSammeVilkaar.setOppholdstillatelseType(nullcheckSetDefaultValue(udiOppholdSammeVilkaar.getOppholdstillatelseType(), UdiOppholdstillatelseType.MIDLERTIDIG));
        udiOppholdSammeVilkaar.setOppholdSammeVilkaarPeriode(nullcheckSetDefaultValue(udiOppholdSammeVilkaar.getOppholdSammeVilkaarPeriode(), UdiPeriode.builder().fra(LocalDate.now()).til(LocalDate.now()).build()));
        udiOppholdSammeVilkaar.setOppholdstillatelseVedtaksDato(nullcheckSetDefaultValue(udiOppholdSammeVilkaar.getOppholdstillatelseVedtaksDato(), LocalDate.now()));
        return udiOppholdSammeVilkaar;
    }
}
