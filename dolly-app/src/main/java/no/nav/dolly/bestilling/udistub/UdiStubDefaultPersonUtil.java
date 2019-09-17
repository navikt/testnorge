package no.nav.dolly.bestilling.udistub;

import static no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOverigIkkeOppholdKategoriType.ANNULERING_AV_VISUM;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import java.time.LocalDate;
import java.util.Collections;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.udistub.model.UdiHarType;
import no.nav.dolly.domain.resultset.udistub.model.UdiPeriode;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.avgjoerelse.UdiAvgjorelse;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiAvslagEllerBortfall;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiGrunnlagEos;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiGrunnlagOverig;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdSammeVilkaar;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdStatus;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdsrettType;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdstillatelseType;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiUtvistMedInnreiseForbud;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiVarighetType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UdiStubDefaultPersonUtil {

    public static void setPersonDefaultsIfUnspecified(UdiPerson udiPerson) {

        //OPPHOLDSSTATUS
        UdiOppholdStatus specifiedUdiOppholdStatus = nullcheckSetDefaultValue(udiPerson.getOppholdStatus(), new UdiOppholdStatus());

        udiPerson.setOppholdStatus(UdiOppholdStatus.builder()
                .uavklart(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getUavklart(), false))
                .udiOppholdSammeVilkaar(oppholdSammeVilkaarDefaultsIfUnspecified(specifiedUdiOppholdStatus))
                .ikkeOppholdstilatelseIkkeVilkaarIkkeVisum(ikkeOppholdstilatelseIkkeVilkaarIkkeVisumIfUnspecified(specifiedUdiOppholdStatus))
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

    private static UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisumIfUnspecified(UdiOppholdStatus udiOppholdStatus) {
        UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum in = nullcheckSetDefaultValue(udiOppholdStatus.getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum(), new UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum());
        return UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum.builder()
                .ovrigIkkeOppholdsKategoriArsak(nullcheckSetDefaultValue(in.getOvrigIkkeOppholdsKategoriArsak(), ANNULERING_AV_VISUM))
                .avslagEllerBortfall(avslagEllerBortfallIfUnspecified(in))
                .utvistMedInnreiseForbud(utvistMedInnreiseForbudIfUnspecified(in))
                .build();
    }

    private static UdiAvslagEllerBortfall avslagEllerBortfallIfUnspecified(UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum udiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        UdiAvslagEllerBortfall udiAvslagEllerBortfall = nullcheckSetDefaultValue(udiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum.getAvslagEllerBortfall(), new UdiAvslagEllerBortfall());
        return UdiAvslagEllerBortfall.builder()
                .avslagGrunnlagOverig(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagGrunnlagOverig(), UdiGrunnlagOverig.FAMILIE))
                .avslagGrunnlagTillatelseGrunnlagEOS(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagEOS(), UdiGrunnlagEos.FAMILIE))
                .avslagOppholdsrettBehandlet(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagOppholdsrettBehandlet(), UdiOppholdsrettType.FAMILIE))
                .avslagOppholdstillatelseBehandletUtreiseFrist(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagOppholdstillatelseUtreiseFrist(), LocalDate.now()))
                .avslagOppholdstillatelseBehandletGrunnlagOvrig(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagOvrig(), UdiGrunnlagOverig.FAMILIE))
                .avslagOppholdstillatelseBehandletGrunnlagEOS(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagGrunnlagTillatelseGrunnlagEOS(), UdiGrunnlagEos.FAMILIE))
                .tilbakeKallVirkningsDato(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getTilbakeKallVirkningsDato(), LocalDate.now()))
                .tilbakeKallUtreiseFrist(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getTilbakeKallUtreiseFrist(), LocalDate.now()))
                .bortfallAvPOellerBOSDato(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getBortfallAvPOellerBOSDato(), LocalDate.now()))
                .avslagOppholdstillatelseUtreiseFrist(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagOppholdstillatelseUtreiseFrist(), LocalDate.now()))
                .formeltVedtakUtreiseFrist(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getFormeltVedtakUtreiseFrist(), LocalDate.now()))
                .build();
    }

    private static UdiUtvistMedInnreiseForbud utvistMedInnreiseForbudIfUnspecified(UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum udiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        UdiUtvistMedInnreiseForbud udiUtvistMedInnreiseForbud = nullcheckSetDefaultValue(udiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum.getUtvistMedInnreiseForbud(), new UdiUtvistMedInnreiseForbud());
        return UdiUtvistMedInnreiseForbud.builder()
                .innreiseForbud(nullcheckSetDefaultValue(udiUtvistMedInnreiseForbud.getInnreiseForbud(), UdiHarType.JA))
                .innreiseForbudVedtaksDato(nullcheckSetDefaultValue(udiUtvistMedInnreiseForbud.getInnreiseForbudVedtaksDato(), LocalDate.now()))
                .varighet(nullcheckSetDefaultValue(udiUtvistMedInnreiseForbud.getVarighet(), UdiVarighetType.ETT_AR))
                .build();
    }
}
