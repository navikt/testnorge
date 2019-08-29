package no.nav.dolly.bestilling.udistub;

import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import no.nav.dolly.domain.resultset.udistub.model.ArbeidsadgangTo;
import no.nav.dolly.domain.resultset.udistub.model.AvgjorelseTo;
import no.nav.dolly.domain.resultset.udistub.model.PeriodeTo;
import no.nav.dolly.domain.resultset.udistub.model.PersonTo;
import no.nav.dolly.domain.resultset.udistub.model.opphold.AvslagEllerBortfallTo;
import no.nav.dolly.domain.resultset.udistub.model.opphold.IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdSammeVilkaarTo;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdStatusTo;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UtvistMedInnreiseForbudTo;

import java.time.LocalDate;
import java.util.Collections;

public class UdiStubDefaultPersonUtil {
    private static final PeriodeTo DEFAULT_PERIODE = new PeriodeTo(LocalDate.now(), LocalDate.now());
    private static final LocalDate DEFAULT_DATE = LocalDate.now();
    private static final String DEFAULT_FAMILE_KODE = "FAMILIE";
    private static final String DEFAULT_PERMANENT_KODE = "PERMANENT";

    private UdiStubDefaultPersonUtil() {
    }

    public static void setPersonDefaultsIfUnspecified(PersonTo udiPerson) {

        //OPPHOLDSSTATUS
        OppholdStatusTo specifiedOppholdStatus = nullcheckSetDefaultValue(udiPerson.getOppholdStatus(), new OppholdStatusTo());

        udiPerson.setOppholdStatus(OppholdStatusTo.builder()
                .uavklart(nullcheckSetDefaultValue(specifiedOppholdStatus.getUavklart(), false))
                .oppholdSammeVilkaar(oppholdSammeVilkaarDefaultsIfUnspecified(specifiedOppholdStatus))
                .ikkeOppholdstilatelseIkkeVilkaarIkkeVisum(ikkeOppholdstilatelseIkkeVilkaarIkkeVisumIfUnspecified(specifiedOppholdStatus))
                .eosEllerEFTAOppholdstillatelse(nullcheckSetDefaultValue(specifiedOppholdStatus.getEosEllerEFTAOppholdstillatelse(), DEFAULT_FAMILE_KODE))
                .eosEllerEFTAOppholdstillatelseEffektuering(nullcheckSetDefaultValue(specifiedOppholdStatus.getEosEllerEFTAOppholdstillatelseEffektuering(), DEFAULT_DATE))
                .eosEllerEFTAOppholdstillatelsePeriode(nullcheckSetDefaultValue(specifiedOppholdStatus.getEosEllerEFTAOppholdstillatelsePeriode(), DEFAULT_PERIODE))

                .eosEllerEFTABeslutningOmOppholdsrett(nullcheckSetDefaultValue(specifiedOppholdStatus.getEosEllerEFTABeslutningOmOppholdsrett(), DEFAULT_FAMILE_KODE))
                .eosEllerEFTABeslutningOmOppholdsrettEffektuering(nullcheckSetDefaultValue(specifiedOppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettEffektuering(), DEFAULT_DATE))
                .eosEllerEFTABeslutningOmOppholdsrettPeriode(nullcheckSetDefaultValue(specifiedOppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode(), DEFAULT_PERIODE))
                .eosEllerEFTAVedtakOmVarigOppholdsrett(nullcheckSetDefaultValue(specifiedOppholdStatus.getEosEllerEFTAOppholdstillatelse(), DEFAULT_FAMILE_KODE))
                .eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering(nullcheckSetDefaultValue(specifiedOppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettEffektuering(), DEFAULT_DATE))
                .eosEllerEFTAOppholdstillatelsePeriode(nullcheckSetDefaultValue(specifiedOppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode(), DEFAULT_PERIODE))
                .build());

        // ARBEIDSADGANG
        ArbeidsadgangTo specifiedArbeidsadgang = nullcheckSetDefaultValue(udiPerson.getArbeidsadgang(), new ArbeidsadgangTo());
        udiPerson.setArbeidsadgang(
                ArbeidsadgangTo.builder()
                        .arbeidsOmfang(nullcheckSetDefaultValue(specifiedArbeidsadgang.getArbeidsOmfang(), "KUN_ARBEID_HELTID"))
                        .harArbeidsAdgang(nullcheckSetDefaultValue(specifiedArbeidsadgang.getHarArbeidsAdgang(), "JA"))
                        .periode(nullcheckSetDefaultValue(specifiedArbeidsadgang.getPeriode(), DEFAULT_PERIODE))
                        .typeArbeidsadgang(nullcheckSetDefaultValue(specifiedArbeidsadgang.getTypeArbeidsadgang(), "GENERELL"))
                        .build());

        // AVGJORELSER
        udiPerson.setAvgjoerelser(nullcheckSetDefaultValue(udiPerson.getAvgjoerelser(), Collections.singletonList(new AvgjorelseTo())));
        udiPerson.setHarOppholdsTillatelse(nullcheckSetDefaultValue(udiPerson.getHarOppholdsTillatelse(), true));
        udiPerson.setSoknadDato(nullcheckSetDefaultValue(udiPerson.getSoknadDato(), LocalDate.of(2005, 5, 5)));
        udiPerson.setAvgjoerelseUavklart(nullcheckSetDefaultValue(udiPerson.getAvgjoerelseUavklart(), false));
    }

    private static OppholdSammeVilkaarTo oppholdSammeVilkaarDefaultsIfUnspecified(OppholdStatusTo oppholdStatus) {
        OppholdSammeVilkaarTo oppholdSammeVilkaar = nullcheckSetDefaultValue(oppholdStatus.getOppholdSammeVilkaar(), new OppholdSammeVilkaarTo());

        oppholdSammeVilkaar.setOppholdSammeVilkaarEffektuering(nullcheckSetDefaultValue(oppholdSammeVilkaar.getOppholdstillatelseVedtaksDato(), DEFAULT_DATE));
        oppholdSammeVilkaar.setOppholdstillatelseType(nullcheckSetDefaultValue(oppholdSammeVilkaar.getOppholdstillatelseType(), DEFAULT_PERMANENT_KODE));
        oppholdSammeVilkaar.setOppholdSammeVilkaarPeriode(nullcheckSetDefaultValue(oppholdSammeVilkaar.getOppholdSammeVilkaarPeriode(), DEFAULT_PERIODE));
        oppholdSammeVilkaar.setOppholdstillatelseVedtaksDato(nullcheckSetDefaultValue(oppholdSammeVilkaar.getOppholdstillatelseVedtaksDato(), DEFAULT_DATE));
        return oppholdSammeVilkaar;
    }

    private static IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo ikkeOppholdstilatelseIkkeVilkaarIkkeVisumIfUnspecified(OppholdStatusTo oppholdStatus) {
        IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo in = nullcheckSetDefaultValue(oppholdStatus.getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum(), new IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo());
        return IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo.builder()
                .ovrigIkkeOppholdsKategoriArsak(nullcheckSetDefaultValue(in.getOvrigIkkeOppholdsKategoriArsak(), "ANNULERING_AV_VISUM"))
                .avslagEllerBortfall(avslagEllerBortFallIfUnspecified(in))
                .utvistMedInnreiseForbud(utvistMedInnreiseForbudIfUnspecified(in))
                .build();
    }

    private static AvslagEllerBortfallTo avslagEllerBortFallIfUnspecified(IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo ikkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        AvslagEllerBortfallTo avslagEllerBortFall = nullcheckSetDefaultValue(ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.getAvslagEllerBortfall(), new AvslagEllerBortfallTo());
        return AvslagEllerBortfallTo.builder()
                .avslagGrunnlagOverig(nullcheckSetDefaultValue(avslagEllerBortFall.getAvslagGrunnlagOverig(), DEFAULT_FAMILE_KODE))
                .avslagGrunnlagTillatelseGrunnlagEOS(nullcheckSetDefaultValue(avslagEllerBortFall.getAvslagOppholdstillatelseBehandletGrunnlagEOS(), DEFAULT_FAMILE_KODE))
                .avslagOppholdsrettBehandlet(nullcheckSetDefaultValue(avslagEllerBortFall.getAvslagOppholdsrettBehandlet(), DEFAULT_FAMILE_KODE))
                .avslagOppholdstillatelseBehandletUtreiseFrist(nullcheckSetDefaultValue(avslagEllerBortFall.getAvslagOppholdstillatelseUtreiseFrist(), DEFAULT_DATE))
                .avslagOppholdstillatelseBehandletGrunnlagOvrig(nullcheckSetDefaultValue(avslagEllerBortFall.getAvslagOppholdstillatelseBehandletGrunnlagOvrig(), DEFAULT_FAMILE_KODE))
                .avslagOppholdstillatelseBehandletGrunnlagEOS(nullcheckSetDefaultValue(avslagEllerBortFall.getAvslagGrunnlagTillatelseGrunnlagEOS(), DEFAULT_FAMILE_KODE))
                .tilbakeKallVirkningsDato(nullcheckSetDefaultValue(avslagEllerBortFall.getTilbakeKallVirkningsDato(), DEFAULT_DATE))
                .tilbakeKallUtreiseFrist(nullcheckSetDefaultValue(avslagEllerBortFall.getTilbakeKallUtreiseFrist(), DEFAULT_DATE))
                .bortfallAvPOellerBOSDato(nullcheckSetDefaultValue(avslagEllerBortFall.getBortfallAvPOellerBOSDato(), DEFAULT_DATE))
                .avslagOppholdstillatelseUtreiseFrist(nullcheckSetDefaultValue(avslagEllerBortFall.getAvslagOppholdstillatelseUtreiseFrist(), DEFAULT_DATE))
                .formeltVedtakUtreiseFrist(nullcheckSetDefaultValue(avslagEllerBortFall.getFormeltVedtakUtreiseFrist(), DEFAULT_DATE))
                .build();
    }

    private static UtvistMedInnreiseForbudTo utvistMedInnreiseForbudIfUnspecified(IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo ikkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        UtvistMedInnreiseForbudTo utvistMedInnreiseForbud = nullcheckSetDefaultValue(ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.getUtvistMedInnreiseForbud(), new UtvistMedInnreiseForbudTo());
        return UtvistMedInnreiseForbudTo.builder()
                .innreiseForbud(nullcheckSetDefaultValue(utvistMedInnreiseForbud.getInnreiseForbud(), "JA"))
                .innreiseForbudVedtaksDato(nullcheckSetDefaultValue(utvistMedInnreiseForbud.getInnreiseForbudVedtaksDato(), DEFAULT_DATE))
                .varighet(nullcheckSetDefaultValue(utvistMedInnreiseForbud.getVarighet(), "ETT_AR"))
                .build();
    }
}
