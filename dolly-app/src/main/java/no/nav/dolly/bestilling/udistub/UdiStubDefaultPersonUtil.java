package no.nav.dolly.bestilling.udistub;

import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import no.nav.dolly.domain.resultset.udistub.model.UdiArbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.UdiAvgjorelse;
import no.nav.dolly.domain.resultset.udistub.model.UdiPeriode;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.opphold.AvslagEllerBortfall;
import no.nav.dolly.domain.resultset.udistub.model.opphold.IkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdSammeVilkaar;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdStatus;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UtvistMedInnreiseForbud;

import java.time.LocalDate;
import java.util.Collections;

public class UdiStubDefaultPersonUtil {
    private static final UdiPeriode DEFAULT_PERIODE = new UdiPeriode(LocalDate.now(), LocalDate.now());
    private static final LocalDate DEFAULT_DATE = LocalDate.now();
    private static final String DEFAULT_FAMILE_KODE = "FAMILIE";
    private static final String DEFAULT_PERMANENT_KODE = "PERMANENT";

    private UdiStubDefaultPersonUtil() {
    }

    public static void setPersonDefaultsIfUnspecified(UdiPerson udiPerson) {

        //OPPHOLDSSTATUS
        OppholdStatus specifiedOppholdStatus = nullcheckSetDefaultValue(udiPerson.getOppholdStatus(), new OppholdStatus());

        udiPerson.setOppholdStatus(OppholdStatus.builder()
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
        UdiArbeidsadgang specifiedArbeidsadgang = nullcheckSetDefaultValue(udiPerson.getArbeidsadgang(), new UdiArbeidsadgang());
        udiPerson.setArbeidsadgang(
                UdiArbeidsadgang.builder()
                        .arbeidsOmfang(nullcheckSetDefaultValue(specifiedArbeidsadgang.getArbeidsOmfang(), "KUN_ARBEID_HELTID"))
                        .harArbeidsAdgang(nullcheckSetDefaultValue(specifiedArbeidsadgang.getHarArbeidsAdgang(), "JA"))
                        .periode(nullcheckSetDefaultValue(specifiedArbeidsadgang.getPeriode(), DEFAULT_PERIODE))
                        .typeArbeidsadgang(nullcheckSetDefaultValue(specifiedArbeidsadgang.getTypeArbeidsadgang(), "GENERELL"))
                        .build());

        // AVGJORELSER
        udiPerson.setAvgjoerelser(nullcheckSetDefaultValue(udiPerson.getAvgjoerelser(), Collections.singletonList(new UdiAvgjorelse())));
        udiPerson.setHarOppholdsTillatelse(nullcheckSetDefaultValue(udiPerson.getHarOppholdsTillatelse(), true));
        udiPerson.setSoknadDato(nullcheckSetDefaultValue(udiPerson.getSoknadDato(), LocalDate.of(2005, 5, 5)));
        udiPerson.setAvgjoerelseUavklart(nullcheckSetDefaultValue(udiPerson.getAvgjoerelseUavklart(), false));
    }

    private static OppholdSammeVilkaar oppholdSammeVilkaarDefaultsIfUnspecified(OppholdStatus oppholdStatus) {
        OppholdSammeVilkaar oppholdSammeVilkaar = nullcheckSetDefaultValue(oppholdStatus.getOppholdSammeVilkaar(), new OppholdSammeVilkaar());

        oppholdSammeVilkaar.setOppholdSammeVilkaarEffektuering(nullcheckSetDefaultValue(oppholdSammeVilkaar.getOppholdstillatelseVedtaksDato(), DEFAULT_DATE));
        oppholdSammeVilkaar.setOppholdstillatelseType(nullcheckSetDefaultValue(oppholdSammeVilkaar.getOppholdstillatelseType(), DEFAULT_PERMANENT_KODE));
        oppholdSammeVilkaar.setOppholdSammeVilkaarPeriode(nullcheckSetDefaultValue(oppholdSammeVilkaar.getOppholdSammeVilkaarPeriode(), DEFAULT_PERIODE));
        oppholdSammeVilkaar.setOppholdstillatelseVedtaksDato(nullcheckSetDefaultValue(oppholdSammeVilkaar.getOppholdstillatelseVedtaksDato(), DEFAULT_DATE));
        return oppholdSammeVilkaar;
    }

    private static IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisumIfUnspecified(OppholdStatus oppholdStatus) {
        IkkeOppholdstilatelseIkkeVilkaarIkkeVisum in = nullcheckSetDefaultValue(oppholdStatus.getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum(), new IkkeOppholdstilatelseIkkeVilkaarIkkeVisum());
        return IkkeOppholdstilatelseIkkeVilkaarIkkeVisum.builder()
                .ovrigIkkeOppholdsKategoriArsak(nullcheckSetDefaultValue(in.getOvrigIkkeOppholdsKategoriArsak(), "ANNULERING_AV_VISUM"))
                .avslagEllerBortfall(avslagEllerBortfallIfUnspecified(in))
                .utvistMedInnreiseForbud(utvistMedInnreiseForbudIfUnspecified(in))
                .build();
    }

    private static AvslagEllerBortfall avslagEllerBortfallIfUnspecified(IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        AvslagEllerBortfall avslagEllerBortfall = nullcheckSetDefaultValue(ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.getAvslagEllerBortfall(), new AvslagEllerBortfall());
        return AvslagEllerBortfall.builder()
                .avslagGrunnlagOverig(nullcheckSetDefaultValue(avslagEllerBortfall.getAvslagGrunnlagOverig(), DEFAULT_FAMILE_KODE))
                .avslagGrunnlagTillatelseGrunnlagEOS(nullcheckSetDefaultValue(avslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagEOS(), DEFAULT_FAMILE_KODE))
                .avslagOppholdsrettBehandlet(nullcheckSetDefaultValue(avslagEllerBortfall.getAvslagOppholdsrettBehandlet(), DEFAULT_FAMILE_KODE))
                .avslagOppholdstillatelseBehandletUtreiseFrist(nullcheckSetDefaultValue(avslagEllerBortfall.getAvslagOppholdstillatelseUtreiseFrist(), DEFAULT_DATE))
                .avslagOppholdstillatelseBehandletGrunnlagOvrig(nullcheckSetDefaultValue(avslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagOvrig(), DEFAULT_FAMILE_KODE))
                .avslagOppholdstillatelseBehandletGrunnlagEOS(nullcheckSetDefaultValue(avslagEllerBortfall.getAvslagGrunnlagTillatelseGrunnlagEOS(), DEFAULT_FAMILE_KODE))
                .tilbakeKallVirkningsDato(nullcheckSetDefaultValue(avslagEllerBortfall.getTilbakeKallVirkningsDato(), DEFAULT_DATE))
                .tilbakeKallUtreiseFrist(nullcheckSetDefaultValue(avslagEllerBortfall.getTilbakeKallUtreiseFrist(), DEFAULT_DATE))
                .bortfallAvPOellerBOSDato(nullcheckSetDefaultValue(avslagEllerBortfall.getBortfallAvPOellerBOSDato(), DEFAULT_DATE))
                .avslagOppholdstillatelseUtreiseFrist(nullcheckSetDefaultValue(avslagEllerBortfall.getAvslagOppholdstillatelseUtreiseFrist(), DEFAULT_DATE))
                .formeltVedtakUtreiseFrist(nullcheckSetDefaultValue(avslagEllerBortfall.getFormeltVedtakUtreiseFrist(), DEFAULT_DATE))
                .build();
    }

    private static UtvistMedInnreiseForbud utvistMedInnreiseForbudIfUnspecified(IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        UtvistMedInnreiseForbud utvistMedInnreiseForbud = nullcheckSetDefaultValue(ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.getUtvistMedInnreiseForbud(), new UtvistMedInnreiseForbud());
        return UtvistMedInnreiseForbud.builder()
                .innreiseForbud(nullcheckSetDefaultValue(utvistMedInnreiseForbud.getInnreiseForbud(), "JA"))
                .innreiseForbudVedtaksDato(nullcheckSetDefaultValue(utvistMedInnreiseForbud.getInnreiseForbudVedtaksDato(), DEFAULT_DATE))
                .varighet(nullcheckSetDefaultValue(utvistMedInnreiseForbud.getVarighet(), "ETT_AR"))
                .build();
    }
}
