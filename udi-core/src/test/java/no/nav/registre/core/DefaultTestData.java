package no.nav.registre.core;

import no.nav.registre.core.database.model.Alias;
import no.nav.registre.core.database.model.Arbeidsadgang;
import no.nav.registre.core.database.model.MangelfullDato;
import no.nav.registre.core.database.model.Periode;
import no.nav.registre.core.database.model.Person;
import no.nav.registre.core.database.model.Avgjorelse;
import no.nav.registre.core.database.model.PersonNavn;
import no.nav.registre.core.database.model.opphold.AvslagEllerBortFall;
import no.nav.registre.core.database.model.opphold.IkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
import no.nav.registre.core.database.model.opphold.OppholdSammeVilkaar;
import no.nav.registre.core.database.model.opphold.OppholdStatus;
import no.nav.registre.core.database.model.opphold.UtvistMedInnreiseForbud;
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

import java.sql.Date;
import java.util.Arrays;

public class DefaultTestData {
	public static final String TEST_PERSON_FNR = "13371337133";
	public static final MangelfullDato TEST_MANGELFULL_DATO = new MangelfullDato(1, 2, 3);
	public static final Date TEST_DATE = Date.valueOf("2000-02-1");
	public static final String TEST_OMGJORT_AVGJORELSE_ID = "123";
	public static final Boolean TEST_ER_POSITIV = true;
	public static final Periode TEST_PERIODE = new Periode(Date.valueOf("2000-01-3"), Date.valueOf("2000-01-4"));
	public static final Integer TEST_VARIGHET = 1;
	public static final Varighet TEST_VARIGHET_UDI = Varighet.ETT_AR;
	public static final String TEST_SAKSNUMMER = "testus maximus";
	public static final String TEST_ETAT = "ETATUS MAXIMUS";
	public static final Boolean TEST_FLYKTNINGSTATUS = true;
	public static final Boolean TEST_UAVKLART_FLYKTNINGSTATUS = false;

	public static final PersonNavn TEST_NAVN = new PersonNavn("GLENN", "ATLE", "GLENNSON");
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

	private static final Person testPerson = createPerson();

	public static final Kodeverk TEST_KODEVERK_CODE = new Kodeverk();

	{
		TEST_KODEVERK_CODE.setKode("testkode");
	}

	public static Person createPerson() {
		Person person = new Person();
		person.setNavn(TEST_NAVN);
		person.setId(TEST_ID);
		person.setFnr(TEST_PERSON_FNR);
		person.setFoedselsDato(TEST_MANGELFULL_DATO);
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
		person.setOppholdsTillatelse(TEST_OPPHOLDSTILLATELSE);
		person.setFlyktning(TEST_FLYKTNINGSTATUS);
		person.setSoeknadOmBeskyttelseUnderBehandling(TEST_SOEKNAD_OM_BESKYTTELSE);
		person.setSoknadDato(TEST_DATE);
		return person;
	}

	public static Avgjorelse.AvgjorelseBuilder createPersonAvgjorelseBuilder() {
		return Avgjorelse.builder()
				.id(TEST_ID)
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
				.flyktningstatus(TEST_FLYKTNINGSTATUS)
				.uavklartFlyktningstatus(TEST_UAVKLART_FLYKTNINGSTATUS)
				.person(testPerson);
	}

	public static Alias.AliasBuilder createAlias() {
		return Alias.builder()
				.fnr(TEST_PERSON_FNR)
				.navn(TEST_NAVN)
				.id(TEST_ID)
				.person(testPerson);
	}

	public static Arbeidsadgang createArbeidsAdgang() {
		Arbeidsadgang arbeidsadgang = new Arbeidsadgang();
		arbeidsadgang.setArbeidsOmfang(TEST_ARBEIDOMGANGKATEGORI);
		arbeidsadgang.setHarArbeidsAdgang(TEST_ARBEIDSADGANG);
		arbeidsadgang.setTypeArbeidsadgang(TEST_ARBEIDSADGANG_TYPE);
		arbeidsadgang.setId(TEST_ID);
		arbeidsadgang.setPerson(testPerson);
		arbeidsadgang.setPeriode(TEST_PERIODE);
		return arbeidsadgang;
	}

	public static OppholdStatus createOppholdStatus() {
		OppholdStatus oppholdStatus = new OppholdStatus();
		oppholdStatus.setId(TEST_ID);
		oppholdStatus.setOppholdSammeVilkaar(createOppholdSammeVilkaar());
		oppholdStatus.setEosEllerEFTABeslutningOmOppholdsrettEffektuering(TEST_DATE);
		oppholdStatus.setEosEllerEFTABeslutningOmOppholdsrettPeriode(TEST_PERIODE);
		oppholdStatus.setEosEllerEFTAOppholdstillatelseEffektuering(TEST_DATE);
		oppholdStatus.setEosEllerEFTAVedtakOmVarigOppholdsrettPeriode(TEST_PERIODE);
		oppholdStatus.setEosEllerEFTABeslutningOmOppholdsrett(TEST_eosEllerEFTAGrunnlagskategoriOppholdsrett);
		oppholdStatus.setEosEllerEFTAOppholdstillatelse(TEST_eosEllerEFTAGrunnlagskategoriOppholdstillatelse);
		oppholdStatus.setEosEllerEFTAVedtakOmVarigOppholdsrett(TEST_eosEllerEFTAGrunnlagskategoriOppholdsrett);
		oppholdStatus.setIkkeOppholdstilatelseIkkeVilkaarIkkeVisum(createIkkeOppholdstilatelseIkkeVilkaarIkkeVisum());
		oppholdStatus.setPerson(testPerson);

		return oppholdStatus;
	}

	public static OppholdSammeVilkaar createOppholdSammeVilkaar() {
		OppholdSammeVilkaar oppholdSammeVilkaar = new OppholdSammeVilkaar();
		oppholdSammeVilkaar.setOppholdSammeVilkaarEffektuering(TEST_DATE);
		oppholdSammeVilkaar.setOppholdSammeVilkaarPeriode(TEST_PERIODE);
		oppholdSammeVilkaar.setOppholdstillatelseType(TEST_OPPHOLDSTILLATELSE_TYPE);
		oppholdSammeVilkaar.setOppholdstillatelseVedtaksDato(TEST_DATE);
		return oppholdSammeVilkaar;
	}

	public static IkkeOppholdstilatelseIkkeVilkaarIkkeVisum createIkkeOppholdstilatelseIkkeVilkaarIkkeVisum() {
		IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum = new IkkeOppholdstilatelseIkkeVilkaarIkkeVisum();
		ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.setAvslagEllerBortFall(createAvslagEllerBortFall());
		ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.setOvrigIkkeOppholdsKategoriArsak(TEST_ovrigIkkeOppholdsKategori);
		ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.setUtvistMedInnreiseForbud(createUtvistMedInnreiseForbud());
		return ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;
	}

	public static AvslagEllerBortFall createAvslagEllerBortFall() {
		AvslagEllerBortFall avslagEllerBortFall = new AvslagEllerBortFall();
		avslagEllerBortFall.setAvgjorelsesDato(TEST_DATE);
		avslagEllerBortFall.setAvslagGrunnlagOverig(TEST_OPPHOLDS_GRUNNLAG_KATEGORI);
		avslagEllerBortFall.setAvslagGrunnlagTillatelseGrunnlagEOS(TEST_eosEllerEFTAGrunnlagskategoriOppholdstillatelse);
		avslagEllerBortFall.setAvslagOppholdstillatelseBehandletGrunnlagOvrig(TEST_OPPHOLDS_GRUNNLAG_KATEGORI);
		avslagEllerBortFall.setAvslagOppholdstillatelseBehandletGrunnlagEOS(TEST_eosEllerEFTAGrunnlagskategoriOppholdstillatelse);
		avslagEllerBortFall.setAvslagOppholdsrettBehandlet(TEST_eosEllerEFTAGrunnlagskategoriOppholdsrett);
		avslagEllerBortFall.setAvslagOppholdstillatelseBehandletUtreiseFrist(TEST_DATE);
		avslagEllerBortFall.setBortfallAvPOellerBOSDato(TEST_DATE);
		avslagEllerBortFall.setTilbakeKallUtreiseFrist(TEST_DATE);
		avslagEllerBortFall.setTilbakeKallVirkningsDato(TEST_DATE);
		avslagEllerBortFall.setFormeltVedtakUtreiseFrist(TEST_DATE);

		return avslagEllerBortFall;
	}

	public static UtvistMedInnreiseForbud createUtvistMedInnreiseForbud() {
		UtvistMedInnreiseForbud utvistMedInnreiseForbud = new UtvistMedInnreiseForbud();
		utvistMedInnreiseForbud.setInnreiseForbud(TEST_INNREISEFORBUD);
		utvistMedInnreiseForbud.setInnreiseForbudVedtaksDato(TEST_DATE);
		utvistMedInnreiseForbud.setVarighet(TEST_VARIGHET_UDI);
		return utvistMedInnreiseForbud;
	}
}
