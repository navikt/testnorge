package no.nav.registre.udistub.core;

import no.nav.registre.udistub.core.database.model.PersonNavn;
import no.nav.registre.udistub.core.service.to.AliasTo;
import no.nav.registre.udistub.core.service.to.ArbeidsadgangTo;
import no.nav.registre.udistub.core.service.to.AvgjorelseTo;
import no.nav.registre.udistub.core.service.to.PeriodeTo;
import no.nav.registre.udistub.core.service.to.PersonTo;
import no.nav.registre.udistub.core.service.to.opphold.AvslagEllerBortfallTo;
import no.nav.registre.udistub.core.service.to.opphold.IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo;
import no.nav.registre.udistub.core.service.to.opphold.OppholdSammeVilkaarTo;
import no.nav.registre.udistub.core.service.to.opphold.OppholdStatusTo;
import no.nav.registre.udistub.core.service.to.opphold.UtvistMedInnreiseForbudTo;
import no.udi.common.v2.Kodeverk;
import no.udi.mt_1067_nav_data.v1.ArbeidOmfangKategori;
import no.udi.mt_1067_nav_data.v1.ArbeidsadgangType;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import no.udi.mt_1067_nav_data.v1.OppholdsgrunnlagKategori;
import no.udi.mt_1067_nav_data.v1.OppholdstillatelseKategori;
import no.udi.mt_1067_nav_data.v1.OvrigIkkeOppholdsKategori;
import no.udi.mt_1067_nav_data.v1.Varighet;

import java.time.LocalDate;
import java.util.Arrays;

public class DefaultTestData {
    public static final String TEST_PERSON_FNR = "19026229432";
    public static final LocalDate TEST_DATE = LocalDate.of(2005, 4, 3);
    public static final String TEST_OMGJORT_AVGJORELSE_ID = "123";
    public static final Boolean TEST_ER_POSITIV = true;
    public static final PeriodeTo TEST_PERIODE = new PeriodeTo(LocalDate.of(2000, 1, 2), LocalDate.of(2001, 2, 3));
    public static final Integer TEST_VARIGHET = 1;
    public static final Varighet TEST_VARIGHET_UDI = Varighet.ETT_AR;
    public static final String TEST_SAKSNUMMER = "testus maximus";
    public static final String TEST_ETAT = "ETATUS MAXIMUS";
    public static final Boolean TEST_FLYKTNINGSTATUS = true;
    public static final Boolean TEST_UAVKLART_FLYKTNINGSTATUS = false;

    public static final PersonNavn TEST_NAVN = new PersonNavn("NATURLIG", "ABSURD", "BÆREPOSE");
    public static final Long TEST_ID = 1L;
    public static final ArbeidOmfangKategori TEST_ARBEIDOMGANGKATEGORI = ArbeidOmfangKategori.DELTID_SAMT_FERIER_HELTID;
    public static final JaNeiUavklart TEST_ARBEIDSADGANG = JaNeiUavklart.NEI;
    public static final ArbeidsadgangType TEST_ARBEIDSADGANG_TYPE = ArbeidsadgangType.GENERELL;

    public static final EOSellerEFTAGrunnlagskategoriOppholdsrett TEST_eosEllerEFTAGrunnlagskategoriOppholdsrett = EOSellerEFTAGrunnlagskategoriOppholdsrett.FAMILIE;
    public static final EOSellerEFTAGrunnlagskategoriOppholdstillatelse TEST_eosEllerEFTAGrunnlagskategoriOppholdstillatelse = EOSellerEFTAGrunnlagskategoriOppholdstillatelse.ARBEID;

    public static final Boolean TEST_AVGJOERELSE_UAVKLART = false;
    public static final Boolean TEST_OPPHOLDSTILLATELSE = true;
    public static final OppholdstillatelseKategori TEST_OPPHOLDSTILLATELSE_TYPE = OppholdstillatelseKategori.PERMANENT;
    public static final JaNeiUavklart TEST_SOEKNAD_OM_BESKYTTELSE = JaNeiUavklart.JA;
    public static final JaNeiUavklart TEST_INNREISEFORBUD = JaNeiUavklart.JA;
    public static final OppholdsgrunnlagKategori TEST_OPPHOLDS_GRUNNLAG_KATEGORI = OppholdsgrunnlagKategori.ARBEID;
    public static final OvrigIkkeOppholdsKategori TEST_ovrigIkkeOppholdsKategori = OvrigIkkeOppholdsKategori.ANNULERING_AV_VISUM;

    private static final PersonTo testPerson = createPersonTo();

    public static final Kodeverk TEST_KODEVERK_CODE = new Kodeverk();

    {
        TEST_KODEVERK_CODE.setKode("testkode");
    }

    public static PersonTo createPersonTo() {
        PersonTo person = new PersonTo();
        person.setIdent(TEST_PERSON_FNR);
        person.setAvgjoerelser(
                Arrays.asList(
                        createPersonAvgjorelseBuilder().build(),
                        createPersonAvgjorelseBuilder().build())
        );
        person.setAliaser(
                Arrays.asList(
                        createAlias().build(),
                        createAlias().build())
        );
        person.setArbeidsadgang(createArbeidsAdgang());
        person.setOppholdStatus(createOppholdStatus());
        person.setAvgjoerelseUavklart(TEST_AVGJOERELSE_UAVKLART);
        person.setHarOppholdsTillatelse(TEST_OPPHOLDSTILLATELSE);
        person.setFlyktning(TEST_FLYKTNINGSTATUS);
        person.setSoeknadOmBeskyttelseUnderBehandling(TEST_SOEKNAD_OM_BESKYTTELSE);
        person.setSoknadDato(TEST_DATE);
        return person;
    }

    public static AvgjorelseTo.AvgjorelseToBuilder createPersonAvgjorelseBuilder() {
        return AvgjorelseTo.builder()
                .avgjoerelsesDato(TEST_DATE)
                .omgjortAvgjoerelsesId(TEST_OMGJORT_AVGJORELSE_ID)
                .grunntypeKode(TEST_KODEVERK_CODE)
                .erPositiv(TEST_ER_POSITIV)
                .tillatelseVarighetKode(TEST_KODEVERK_CODE)
                .tillatelseVarighet(TEST_VARIGHET)
                .tillatelseKode(TEST_KODEVERK_CODE)
                .tillatelseVarighet(TEST_VARIGHET)
                .utfallstypeKode(TEST_KODEVERK_CODE)
                .utfallVarighetKode(TEST_KODEVERK_CODE)
                .utfallVarighet(TEST_VARIGHET)
                .utfallPeriode(TEST_PERIODE)
                .effektueringsDato(TEST_DATE)
                .iverksettelseDato(TEST_DATE)
                .utreisefristDato(TEST_DATE)
                .saksnummer(TEST_SAKSNUMMER)
                .etat(TEST_ETAT)
                .harFlyktningstatus(TEST_FLYKTNINGSTATUS)
                .uavklartFlyktningstatus(TEST_UAVKLART_FLYKTNINGSTATUS)
                .person(testPerson);
    }

    public static AliasTo.AliasToBuilder createAlias() {
        return AliasTo.builder()
                .person(testPerson);
    }

    public static ArbeidsadgangTo createArbeidsAdgang() {
        ArbeidsadgangTo arbeidsadgang = new ArbeidsadgangTo();
        arbeidsadgang.setArbeidsOmfang(TEST_ARBEIDOMGANGKATEGORI);
        arbeidsadgang.setHarArbeidsAdgang(TEST_ARBEIDSADGANG);
        arbeidsadgang.setTypeArbeidsadgang(TEST_ARBEIDSADGANG_TYPE);
        arbeidsadgang.setPerson(testPerson);
        arbeidsadgang.setPeriode(TEST_PERIODE);
        return arbeidsadgang;
    }

    public static OppholdStatusTo createOppholdStatus() {
        OppholdStatusTo oppholdStatus = new OppholdStatusTo();
        oppholdStatus.setOppholdSammeVilkaar(createOppholdSammeVilkaar());
        oppholdStatus.setEosEllerEFTABeslutningOmOppholdsrettEffektuering(TEST_DATE);
        oppholdStatus.setEosEllerEFTABeslutningOmOppholdsrettPeriode(TEST_PERIODE);
        oppholdStatus.setEosEllerEFTABeslutningOmOppholdsrett(TEST_eosEllerEFTAGrunnlagskategoriOppholdsrett);

        oppholdStatus.setEosEllerEFTAOppholdstillatelseEffektuering(TEST_DATE);
        oppholdStatus.setEosEllerEFTAOppholdstillatelsePeriode(TEST_PERIODE);
        oppholdStatus.setEosEllerEFTAOppholdstillatelse(TEST_eosEllerEFTAGrunnlagskategoriOppholdstillatelse);

        oppholdStatus.setEosEllerEFTAVedtakOmVarigOppholdsrettEffektuering(TEST_DATE);
        oppholdStatus.setEosEllerEFTAVedtakOmVarigOppholdsrettPeriode(TEST_PERIODE);
        oppholdStatus.setEosEllerEFTAVedtakOmVarigOppholdsrett(TEST_eosEllerEFTAGrunnlagskategoriOppholdsrett);
        oppholdStatus.setIkkeOppholdstilatelseIkkeVilkaarIkkeVisum(createIkkeOppholdstilatelseIkkeVilkaarIkkeVisum());
        oppholdStatus.setPerson(testPerson);

        return oppholdStatus;
    }

    public static OppholdSammeVilkaarTo createOppholdSammeVilkaar() {
        OppholdSammeVilkaarTo oppholdSammeVilkaar = new OppholdSammeVilkaarTo();
        oppholdSammeVilkaar.setOppholdSammeVilkaarEffektuering(TEST_DATE);
        oppholdSammeVilkaar.setOppholdSammeVilkaarPeriode(TEST_PERIODE);
        oppholdSammeVilkaar.setOppholdstillatelseType(TEST_OPPHOLDSTILLATELSE_TYPE);
        oppholdSammeVilkaar.setOppholdstillatelseVedtaksDato(TEST_DATE);
        return oppholdSammeVilkaar;
    }

    public static IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo createIkkeOppholdstilatelseIkkeVilkaarIkkeVisum() {
        IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo ikkeOppholdstilatelseIkkeVilkaarIkkeVisum = new IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo();
        ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.setAvslagEllerBortfall(createAvslagEllerBortfall());
        ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.setOvrigIkkeOppholdsKategoriArsak(TEST_ovrigIkkeOppholdsKategori);
        ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.setUtvistMedInnreiseForbud(createUtvistMedInnreiseForbud());
        return ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;
    }

    public static AvslagEllerBortfallTo createAvslagEllerBortfall() {
        AvslagEllerBortfallTo avslagEllerBortfall = new AvslagEllerBortfallTo();
        avslagEllerBortfall.setAvgjorelsesDato(TEST_DATE);
        avslagEllerBortfall.setAvslagGrunnlagOverig(TEST_OPPHOLDS_GRUNNLAG_KATEGORI);
        avslagEllerBortfall.setAvslagGrunnlagTillatelseGrunnlagEOS(TEST_eosEllerEFTAGrunnlagskategoriOppholdstillatelse);
        avslagEllerBortfall.setAvslagOppholdstillatelseBehandletGrunnlagOvrig(TEST_OPPHOLDS_GRUNNLAG_KATEGORI);
        avslagEllerBortfall.setAvslagOppholdstillatelseBehandletGrunnlagEOS(TEST_eosEllerEFTAGrunnlagskategoriOppholdstillatelse);
        avslagEllerBortfall.setAvslagOppholdsrettBehandlet(TEST_eosEllerEFTAGrunnlagskategoriOppholdsrett);
        avslagEllerBortfall.setAvslagOppholdstillatelseBehandletUtreiseFrist(TEST_DATE);
        avslagEllerBortfall.setBortfallAvPOellerBOSDato(TEST_DATE);
        avslagEllerBortfall.setTilbakeKallUtreiseFrist(TEST_DATE);
        avslagEllerBortfall.setTilbakeKallVirkningsDato(TEST_DATE);
        avslagEllerBortfall.setFormeltVedtakUtreiseFrist(TEST_DATE);
        return avslagEllerBortfall;
    }

    public static UtvistMedInnreiseForbudTo createUtvistMedInnreiseForbud() {
        UtvistMedInnreiseForbudTo utvistMedInnreiseForbud = new UtvistMedInnreiseForbudTo();
        utvistMedInnreiseForbud.setInnreiseForbud(TEST_INNREISEFORBUD);
        utvistMedInnreiseForbud.setInnreiseForbudVedtaksDato(TEST_DATE);
        utvistMedInnreiseForbud.setVarighet(TEST_VARIGHET_UDI);
        return utvistMedInnreiseForbud;
    }
}
