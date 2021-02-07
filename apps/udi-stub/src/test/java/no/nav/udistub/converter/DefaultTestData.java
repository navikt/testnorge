package no.nav.udistub.converter;

import no.nav.udistub.service.dto.UdiAlias;
import no.nav.udistub.service.dto.UdiArbeidsadgang;
import no.nav.udistub.service.dto.UdiAvgjorelse;
import no.nav.udistub.service.dto.UdiPeriode;
import no.nav.udistub.service.dto.UdiPerson;
import no.nav.udistub.service.dto.opphold.UdiAvslagEllerBortfall;
import no.nav.udistub.service.dto.opphold.UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
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

import no.nav.udistub.database.model.Kodeverk;
import no.nav.udistub.database.model.PersonNavn;
import no.nav.udistub.service.dto.opphold.UdiOppholdSammeVilkaar;
import no.nav.udistub.service.dto.opphold.UdiOppholdStatus;
import no.nav.udistub.service.dto.opphold.UdiUtvistMedInnreiseForbud;

public class DefaultTestData {
    public static final String TEST_PERSON_FNR = "19026229432";
    public static final String TEST_PERSON_ALIAS_FNR = "19026212345";
    public static final LocalDate TEST_DATE = LocalDate.of(1962, 2, 19);
    public static final String TEST_OMGJORT_AVGJORELSE_ID = "123";
    public static final Boolean TEST_ER_POSITIV = true;
    public static final UdiPeriode TEST_PERIODE = new UdiPeriode(LocalDate.of(2000, 1, 2), LocalDate.of(2001, 2, 3));
    public static final Integer TEST_VARIGHET = 1;
    public static final Varighet TEST_VARIGHET_UDI = Varighet.ETT_AR;
    public static final String TEST_SAKSNUMMER = "testus maximus";
    public static final String TEST_ETAT = "ETATUS MAXIMUS";
    public static final Boolean TEST_FLYKTNINGSTATUS = true;
    public static final Boolean TEST_UAVKLART_FLYKTNINGSTATUS = false;

    public static final PersonNavn TEST_NAVN = new PersonNavn("NATURLIG", "ABSURD", "BÆREPOSE");
    public static final ArbeidOmfangKategori TEST_ARBEIDOMGANGKATEGORI = ArbeidOmfangKategori.DELTID_SAMT_FERIER_HELTID;
    public static final JaNeiUavklart TEST_ARBEIDSADGANG = JaNeiUavklart.NEI;
    public static final ArbeidsadgangType TEST_ARBEIDSADGANG_TYPE = ArbeidsadgangType.GENERELL;

    public static final EOSellerEFTAGrunnlagskategoriOppholdsrett TEST_eosEllerEFTAGrunnlagskategoriOppholdsrett = EOSellerEFTAGrunnlagskategoriOppholdsrett.FAMILIE;
    public static final EOSellerEFTAGrunnlagskategoriOppholdstillatelse TEST_eosEllerEFTAGrunnlagskategoriOppholdstillatelse = EOSellerEFTAGrunnlagskategoriOppholdstillatelse.ARBEID;

    public static final Boolean TEST_AVGJOERELSE_UAVKLART = false;
    public static final Boolean TEST_OPPHOLDSTILLATELSE = true;
    public static final JaNeiUavklart TEST_SOEKNAD_OM_BESKYTTELSE = JaNeiUavklart.JA;
    public static final JaNeiUavklart TEST_INNREISEFORBUD = JaNeiUavklart.JA;
    public static final OppholdsgrunnlagKategori TEST_OPPHOLDS_GRUNNLAG_KATEGORI = OppholdsgrunnlagKategori.ARBEID;
    public static final OvrigIkkeOppholdsKategori TEST_ovrigIkkeOppholdsKategori = OvrigIkkeOppholdsKategori.ANNULERING_AV_VISUM;
    public static final OppholdstillatelseKategori TEST_OPPHOLDSTILLATELSE_TYPE = OppholdstillatelseKategori.PERMANENT;

    private static final UdiPerson testPerson = createPersonTo();

    public static final Kodeverk TEST_KODEVERK_CODE = new Kodeverk();

    static {
        TEST_KODEVERK_CODE.setKode("testkode");
        TEST_KODEVERK_CODE.setType(null);
        TEST_KODEVERK_CODE.setVisningsnavn(null);
    }

    public static UdiPerson createPersonTo() {
        UdiPerson person = new UdiPerson();
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

    public static UdiAvgjorelse.UdiAvgjorelseBuilder createPersonAvgjorelseBuilder() {
        return UdiAvgjorelse.builder()
                .avgjoerelsesDato(TEST_DATE)
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
                .uavklartFlyktningstatus(TEST_UAVKLART_FLYKTNINGSTATUS);
    }

    public static UdiAlias.UdiAliasBuilder createAlias() {
        return UdiAlias.builder();
    }

    public static UdiArbeidsadgang createArbeidsAdgang() {
        UdiArbeidsadgang arbeidsadgang = new UdiArbeidsadgang();
        arbeidsadgang.setArbeidsOmfang(TEST_ARBEIDOMGANGKATEGORI);
        arbeidsadgang.setHarArbeidsAdgang(TEST_ARBEIDSADGANG);
        arbeidsadgang.setTypeArbeidsadgang(TEST_ARBEIDSADGANG_TYPE);
        arbeidsadgang.setPeriode(TEST_PERIODE);
        return arbeidsadgang;
    }

    public static UdiOppholdStatus createOppholdStatus() {
        UdiOppholdStatus oppholdStatus = new UdiOppholdStatus();
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

        return oppholdStatus;
    }

    public static UdiOppholdSammeVilkaar createOppholdSammeVilkaar() {
        UdiOppholdSammeVilkaar oppholdSammeVilkaar = new UdiOppholdSammeVilkaar();
        oppholdSammeVilkaar.setOppholdSammeVilkaarEffektuering(TEST_DATE);
        oppholdSammeVilkaar.setOppholdSammeVilkaarPeriode(TEST_PERIODE);
        oppholdSammeVilkaar.setOppholdstillatelseType(TEST_OPPHOLDSTILLATELSE_TYPE);
        oppholdSammeVilkaar.setOppholdstillatelseVedtaksDato(TEST_DATE);
        return oppholdSammeVilkaar;
    }

    public static UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum createIkkeOppholdstilatelseIkkeVilkaarIkkeVisum() {
        UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum = new UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum();
        ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.setAvslagEllerBortfall(createAvslagEllerBortfall());
        ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.setOvrigIkkeOppholdsKategoriArsak(TEST_ovrigIkkeOppholdsKategori);
        ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.setUtvistMedInnreiseForbud(createUtvistMedInnreiseForbud());
        return ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;
    }

    public static UdiAvslagEllerBortfall createAvslagEllerBortfall() {
        UdiAvslagEllerBortfall avslagEllerBortfall = new UdiAvslagEllerBortfall();
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

    public static UdiUtvistMedInnreiseForbud createUtvistMedInnreiseForbud() {
        UdiUtvistMedInnreiseForbud utvistMedInnreiseForbud = new UdiUtvistMedInnreiseForbud();
        utvistMedInnreiseForbud.setInnreiseForbud(TEST_INNREISEFORBUD);
        utvistMedInnreiseForbud.setInnreiseForbudVedtaksDato(TEST_DATE);
        utvistMedInnreiseForbud.setVarighet(TEST_VARIGHET_UDI);
        return utvistMedInnreiseForbud;
    }
}
