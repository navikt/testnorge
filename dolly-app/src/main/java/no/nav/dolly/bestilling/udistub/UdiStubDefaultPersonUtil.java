package no.nav.dolly.bestilling.udistub;

import static java.time.LocalDate.now;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import no.nav.dolly.domain.resultset.udistub.model.Arbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.Avgjoerelse;
import no.nav.dolly.domain.resultset.udistub.model.Periode;
import no.nav.dolly.domain.resultset.udistub.model.Person;
import no.nav.dolly.domain.resultset.udistub.model.opphold.AvslagEllerBortFall;
import no.nav.dolly.domain.resultset.udistub.model.opphold.IkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdSammeVilkaar;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdStatus;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UtvistMedInnreiseForbud;

import java.sql.Date;
import java.util.Collections;

public class UdiStubDefaultPersonUtil {
    private static final Periode DEFAULT_PERIODE = new Periode(Date.valueOf(now()), Date.valueOf(now()));
    private static final Date DEFAULT_DATE = Date.valueOf(now());
    private static final String DEFAULT_FAMILE_KODE = "FAMILIE";
    private static final String DEFAULT_PERMANENT_KODE = "PERMANENT";

    private UdiStubDefaultPersonUtil() {
    }

    public static void setPersonDefaultsIfUnspecified(Person udiPerson) {

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
        Arbeidsadgang specifiedArbeidsadgang = nullcheckSetDefaultValue(udiPerson.getArbeidsadgang(), new Arbeidsadgang());
        udiPerson.setArbeidsadgang(
                Arbeidsadgang.builder()
                        .arbeidsOmfang(nullcheckSetDefaultValue(specifiedArbeidsadgang.getArbeidsOmfang(), "KunArbeidHeltid"))
                        .harArbeidsAdgang(nullcheckSetDefaultValue(specifiedArbeidsadgang.getHarArbeidsAdgang(), "Ja"))
                        .periode(nullcheckSetDefaultValue(specifiedArbeidsadgang.getPeriode(), DEFAULT_PERIODE))
                        .typeArbeidsadgang(nullcheckSetDefaultValue(specifiedArbeidsadgang.getTypeArbeidsadgang(), "BestemtArbeidEllerOppdrag"))
                        .build());

        // AVGJORELSER
        udiPerson.setAvgjoerelser(nullcheckSetDefaultValue(udiPerson.getAvgjoerelser(), Collections.singletonList(new Avgjoerelse())));
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
                .avslagEllerBortFall(avslagEllerBortFallIfUnspecified(in))
                .utvistMedInnreiseForbud(utvistMedInnreiseForbudIfUnspecified(in))
                .build();
    }

    private static AvslagEllerBortFall avslagEllerBortFallIfUnspecified(IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        AvslagEllerBortFall avslagEllerBortFall = nullcheckSetDefaultValue(ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.getAvslagEllerBortFall(), new AvslagEllerBortFall());
        return AvslagEllerBortFall.builder()
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

    private static UtvistMedInnreiseForbud utvistMedInnreiseForbudIfUnspecified(IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
        UtvistMedInnreiseForbud utvistMedInnreiseForbud = nullcheckSetDefaultValue(ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.getUtvistMedInnreiseForbud(), new UtvistMedInnreiseForbud());
        return UtvistMedInnreiseForbud.builder()
                .innreiseForbud(nullcheckSetDefaultValue(utvistMedInnreiseForbud.getInnreiseForbud(), "JA"))
                .innreiseForbudVedtaksDato(nullcheckSetDefaultValue(utvistMedInnreiseForbud.getInnreiseForbudVedtaksDato(), DEFAULT_DATE))
                .varighet(nullcheckSetDefaultValue(utvistMedInnreiseForbud.getVarighet(), "ETT_AR"))
                .build();
    }
}
