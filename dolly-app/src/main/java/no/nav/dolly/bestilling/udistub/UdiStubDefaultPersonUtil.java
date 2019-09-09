package no.nav.dolly.bestilling.udistub;

import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import no.nav.dolly.domain.resultset.udistub.model.UdiArbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.UdiAvgjorelse;
import no.nav.dolly.domain.resultset.udistub.model.UdiPeriode;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiAvslagEllerBortfall;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdSammeVilkaar;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdStatus;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiUtvistMedInnreiseForbud;

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
        UdiOppholdStatus specifiedUdiOppholdStatus = nullcheckSetDefaultValue(udiPerson.getOppholdStatus(), new UdiOppholdStatus());

        udiPerson.setOppholdStatus(UdiOppholdStatus.builder()
                .uavklart(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getUavklart(), false))
                .udiOppholdSammeVilkaar(oppholdSammeVilkaarDefaultsIfUnspecified(specifiedUdiOppholdStatus))
                .udiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum(ikkeOppholdstilatelseIkkeVilkaarIkkeVisumIfUnspecified(specifiedUdiOppholdStatus))
                .eosEllerEFTAOppholdstillatelse(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getEosEllerEFTAOppholdstillatelse(), DEFAULT_FAMILE_KODE))
                .eosEllerEFTAOppholdstillatelseEffektuering(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getEosEllerEFTAOppholdstillatelseEffektuering(), DEFAULT_DATE))
                .eosEllerEFTAOppholdstillatelsePeriode(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getEosEllerEFTAOppholdstillatelsePeriode(), DEFAULT_PERIODE))

                .eosEllerEFTABeslutningOmOppholdsrett(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getEosEllerEFTABeslutningOmOppholdsrett(), DEFAULT_FAMILE_KODE))
                .eosEllerEFTABeslutningOmOppholdsrettEffektuering(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettEffektuering(), DEFAULT_DATE))
                .eosEllerEFTABeslutningOmOppholdsrettPeriode(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode(), DEFAULT_PERIODE))
                .eosEllerEFTAVedtakOmVarigOppholdsrett(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getEosEllerEFTAOppholdstillatelse(), DEFAULT_FAMILE_KODE))
                .eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettEffektuering(), DEFAULT_DATE))
                .eosEllerEFTAOppholdstillatelsePeriode(nullcheckSetDefaultValue(specifiedUdiOppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode(), DEFAULT_PERIODE))
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

    private static UdiOppholdSammeVilkaar oppholdSammeVilkaarDefaultsIfUnspecified(UdiOppholdStatus udiOppholdStatus) {
        UdiOppholdSammeVilkaar udiOppholdSammeVilkaar = nullcheckSetDefaultValue(udiOppholdStatus.getUdiOppholdSammeVilkaar(), new UdiOppholdSammeVilkaar());

        udiOppholdSammeVilkaar.setOppholdSammeVilkaarEffektuering(nullcheckSetDefaultValue(udiOppholdSammeVilkaar.getOppholdstillatelseVedtaksDato(), DEFAULT_DATE));
        udiOppholdSammeVilkaar.setOppholdstillatelseType(nullcheckSetDefaultValue(udiOppholdSammeVilkaar.getOppholdstillatelseType(), DEFAULT_PERMANENT_KODE));
        udiOppholdSammeVilkaar.setOppholdSammeVilkaarPeriode(nullcheckSetDefaultValue(udiOppholdSammeVilkaar.getOppholdSammeVilkaarPeriode(), DEFAULT_PERIODE));
        udiOppholdSammeVilkaar.setOppholdstillatelseVedtaksDato(nullcheckSetDefaultValue(udiOppholdSammeVilkaar.getOppholdstillatelseVedtaksDato(), DEFAULT_DATE));
        return udiOppholdSammeVilkaar;
    }

    private static UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisumIfUnspecified(UdiOppholdStatus udiOppholdStatus) {
        UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum in = nullcheckSetDefaultValue(udiOppholdStatus.getUdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum(), new UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum());
        return UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum.builder()
                .ovrigIkkeOppholdsKategoriArsak(nullcheckSetDefaultValue(in.getOvrigIkkeOppholdsKategoriArsak(), "ANNULERING_AV_VISUM"))
                .udiAvslagEllerBortfall(avslagEllerBortfallIfUnspecified(in))
                .udiUtvistMedInnreiseForbud(utvistMedInnreiseForbudIfUnspecified(in))
                .build();
    }

    private static UdiAvslagEllerBortfall avslagEllerBortfallIfUnspecified(UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum udiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        UdiAvslagEllerBortfall udiAvslagEllerBortfall = nullcheckSetDefaultValue(udiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum.getUdiAvslagEllerBortfall(), new UdiAvslagEllerBortfall());
        return UdiAvslagEllerBortfall.builder()
                .avslagGrunnlagOverig(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagGrunnlagOverig(), DEFAULT_FAMILE_KODE))
                .avslagGrunnlagTillatelseGrunnlagEOS(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagEOS(), DEFAULT_FAMILE_KODE))
                .avslagOppholdsrettBehandlet(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagOppholdsrettBehandlet(), DEFAULT_FAMILE_KODE))
                .avslagOppholdstillatelseBehandletUtreiseFrist(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagOppholdstillatelseUtreiseFrist(), DEFAULT_DATE))
                .avslagOppholdstillatelseBehandletGrunnlagOvrig(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagOvrig(), DEFAULT_FAMILE_KODE))
                .avslagOppholdstillatelseBehandletGrunnlagEOS(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagGrunnlagTillatelseGrunnlagEOS(), DEFAULT_FAMILE_KODE))
                .tilbakeKallVirkningsDato(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getTilbakeKallVirkningsDato(), DEFAULT_DATE))
                .tilbakeKallUtreiseFrist(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getTilbakeKallUtreiseFrist(), DEFAULT_DATE))
                .bortfallAvPOellerBOSDato(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getBortfallAvPOellerBOSDato(), DEFAULT_DATE))
                .avslagOppholdstillatelseUtreiseFrist(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getAvslagOppholdstillatelseUtreiseFrist(), DEFAULT_DATE))
                .formeltVedtakUtreiseFrist(nullcheckSetDefaultValue(udiAvslagEllerBortfall.getFormeltVedtakUtreiseFrist(), DEFAULT_DATE))
                .build();
    }

    private static UdiUtvistMedInnreiseForbud utvistMedInnreiseForbudIfUnspecified(UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum udiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        UdiUtvistMedInnreiseForbud udiUtvistMedInnreiseForbud = nullcheckSetDefaultValue(udiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum.getUdiUtvistMedInnreiseForbud(), new UdiUtvistMedInnreiseForbud());
        return UdiUtvistMedInnreiseForbud.builder()
                .innreiseForbud(nullcheckSetDefaultValue(udiUtvistMedInnreiseForbud.getInnreiseForbud(), "JA"))
                .innreiseForbudVedtaksDato(nullcheckSetDefaultValue(udiUtvistMedInnreiseForbud.getInnreiseForbudVedtaksDato(), DEFAULT_DATE))
                .varighet(nullcheckSetDefaultValue(udiUtvistMedInnreiseForbud.getVarighet(), "ETT_AR"))
                .build();
    }
}
