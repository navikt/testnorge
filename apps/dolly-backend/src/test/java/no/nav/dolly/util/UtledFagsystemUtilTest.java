package no.nav.dolly.util;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.BestilteKriterier;
import no.nav.dolly.domain.resultset.RsNomData;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.bestilling.skattekort.domain.ArbeidstakerSkatt;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.etterlatte.EtterlatteYtelse;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.domain.resultset.henvendelse.RsHenvendelse;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekter;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.medl.RsMedl;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

class UtledFagsystemUtilTest {

    private static final String FNR = "01014512345";
    private static final String NPID = "01214512345";
    private static final String TESTNORGE_FNR = "01814512345";

    private static Bestilling bestilling() {
        return Bestilling.builder().build();
    }

    private static Bestilling importBestilling() {
        return Bestilling.builder().pdlImport(FNR).build();
    }

    private static Bestilling bestillingMedMiljoer() {
        return Bestilling.builder().miljoer("q1,q2").build();
    }

    private static PdlPersondata pdldataMedNyPerson(Identtype identtype) {

        var pdldata = new PdlPersondata();
        pdldata.setOpprettNyPerson(PdlPersondata.PdlPerson.builder()
                .identtype(identtype)
                .build());
        return pdldata;
    }

    private static PdlPersondata pdldataMedPerson() {

        var pdldata = new PdlPersondata();
        pdldata.setPerson(new PersonDTO());
        return pdldata;
    }

    @Test
    void shouldIncludePenForvalterAndPersonstatusForEmptyBestilling() {

        var result = UtledFagsystemUtil.resolve(BestilteKriterier.builder().build(), bestilling());

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(SystemTyper.PEN_FORVALTER, SystemTyper.PDL_PERSONSTATUS));
    }

    @Test
    void shouldResolvePdlTyperForOpprettNyPerson() {

        var kriterier = BestilteKriterier.builder()
                .pdldata(pdldataMedNyPerson(Identtype.FNR))
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.PDL_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PDL_ORDRE));
        assertThat(result, hasItem(SystemTyper.PDL_PERSONSTATUS));
    }

    @Test
    void shouldResolvePdlTyperForOpprettFraIdenter() {

        var result = UtledFagsystemUtil.resolve(BestilteKriterier.builder().build(),
                Bestilling.builder().opprettFraIdenter(FNR).build());

        assertThat(result, hasItem(SystemTyper.PDL_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PDL_ORDRE));
    }

    @Test
    void shouldNotResolvePdlForvalterForLeggTilWithoutPerson() {

        var kriterier = BestilteKriterier.builder()
                .pdldata(new PdlPersondata())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, Bestilling.builder().ident(FNR).build());

        assertThat(result, not(hasItem(SystemTyper.PDL_FORVALTER)));
        assertThat(result, not(hasItem(SystemTyper.PDL_ORDRE)));
        assertThat(result, hasItem(SystemTyper.PDL_PERSONSTATUS));
    }

    @Test
    void shouldResolvePdlTyperForLeggTilWithPerson() {

        var kriterier = BestilteKriterier.builder()
                .pdldata(pdldataMedPerson())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, Bestilling.builder().ident(FNR).build());

        assertThat(result, hasItem(SystemTyper.PDL_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PDL_ORDRE));
    }

    @Test
    void shouldResolvePdlImportTyper() {

        var result = UtledFagsystemUtil.resolve(BestilteKriterier.builder().build(), importBestilling());

        assertThat(result, hasItem(SystemTyper.PDLIMPORT));
        assertThat(result, hasItem(SystemTyper.PDL_PERSONSTATUS));
        assertThat(result, not(hasItem(SystemTyper.PDL_FORVALTER)));
        assertThat(result, not(hasItem(SystemTyper.PDL_ORDRE)));
    }

    @Test
    void shouldResolvePdlForvalterForImportWithPerson() {

        var kriterier = BestilteKriterier.builder()
                .pdldata(pdldataMedPerson())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, importBestilling());

        assertThat(result, hasItem(SystemTyper.PDLIMPORT));
        assertThat(result, hasItem(SystemTyper.PDL_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PDL_ORDRE));
    }

    @Test
    void shouldNotResolvePenForvalterForNyNpid() {

        var kriterier = BestilteKriterier.builder()
                .pdldata(pdldataMedNyPerson(Identtype.NPID))
                .pensjonforvalter(PensjonData.builder()
                        .inntekt(new PensjonData.PoppInntekt())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestillingMedMiljoer());

        assertThat(result, not(hasItem(SystemTyper.PEN_FORVALTER)));
        assertThat(result, not(hasItem(SystemTyper.PEN_INNTEKT)));
    }

    @Test
    void shouldNotResolvePenForvalterForNpidIdent() {

        var result = UtledFagsystemUtil.resolve(BestilteKriterier.builder().build(),
                Bestilling.builder().ident(NPID).build());

        assertThat(result, not(hasItem(SystemTyper.PEN_FORVALTER)));
    }

    @Test
    void shouldResolvePenForvalterWhenNotAllIdenterAreNpid() {

        var result = UtledFagsystemUtil.resolve(BestilteKriterier.builder().build(),
                Bestilling.builder().opprettFraIdenter(NPID + "," + FNR).build());

        assertThat(result, hasItem(SystemTyper.PEN_FORVALTER));
    }

    @Test
    void shouldResolveAllPensjonSubTyper() {

        var kriterier = BestilteKriterier.builder()
                .pensjonforvalter(PensjonData.builder()
                        .inntekt(new PensjonData.PoppInntekt())
                        .tp(List.of(tpOrdningMedYtelse()))
                        .alderspensjon(new PensjonData.Alderspensjon())
                        .alderspensjonNyUtaksgrad(new PensjonData.AlderspensjonNyUtaksgrad())
                        .uforetrygd(new PensjonData.Uforetrygd())
                        .pensjonsavtale(List.of(new PensjonData.Pensjonsavtale()))
                        .afpOffentlig(new PensjonData.AfpOffentlig())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestillingMedMiljoer());

        assertThat(result, hasItem(SystemTyper.PEN_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PEN_INNTEKT));
        assertThat(result, hasItem(SystemTyper.TP_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PEN_AP));
        assertThat(result, hasItem(SystemTyper.PEN_AP_NY_UTTAKSGRAD));
        assertThat(result, hasItem(SystemTyper.PEN_UT));
        assertThat(result, hasItem(SystemTyper.PEN_PENSJONSAVTALE));
        assertThat(result, hasItem(SystemTyper.PEN_AFP_OFFENTLIG));
    }

    @Test
    void shouldNotResolveTpForvalterWithoutYtelser() {

        var kriterier = BestilteKriterier.builder()
                .pensjonforvalter(PensjonData.builder()
                        .tp(List.of(new PensjonData.TpOrdning()))
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestillingMedMiljoer());

        assertThat(result, not(hasItem(SystemTyper.TP_FORVALTER)));
    }

    private static PensjonData.TpOrdning tpOrdningMedYtelse() {

        var tpOrdning = new PensjonData.TpOrdning();
        tpOrdning.setYtelser(List.of(new PensjonData.TpYtelse()));
        return tpOrdning;
    }

    @Test
    void shouldNotResolvePensjonSubTyperWithoutMiljoer() {

        var kriterier = BestilteKriterier.builder()
                .pensjonforvalter(PensjonData.builder()
                        .inntekt(new PensjonData.PoppInntekt())
                        .alderspensjon(new PensjonData.Alderspensjon())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.PEN_FORVALTER));
        assertThat(result, not(hasItem(SystemTyper.PEN_INNTEKT)));
        assertThat(result, not(hasItem(SystemTyper.PEN_AP)));
    }

    @Test
    void shouldNotResolveNyUttaksgradForGjenopprett() {

        var kriterier = BestilteKriterier.builder()
                .pensjonforvalter(PensjonData.builder()
                        .alderspensjonNyUtaksgrad(new PensjonData.AlderspensjonNyUtaksgrad())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier,
                Bestilling.builder().opprettetFraId(1L).miljoer("q1").build());

        assertThat(result, not(hasItem(SystemTyper.PEN_AP_NY_UTTAKSGRAD)));
    }

    @Test
    void shouldNotResolvePensjonSubTyperWhenFieldsAreNull() {

        var kriterier = BestilteKriterier.builder()
                .pensjonforvalter(PensjonData.builder().build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.PEN_FORVALTER));
        assertThat(result, not(hasItem(SystemTyper.PEN_INNTEKT)));
        assertThat(result, not(hasItem(SystemTyper.TP_FORVALTER)));
        assertThat(result, not(hasItem(SystemTyper.PEN_AP)));
        assertThat(result, not(hasItem(SystemTyper.PEN_UT)));
    }

    @Test
    void shouldResolveSimpleFagsystemer() {

        var kriterier = BestilteKriterier.builder()
                .krrstub(new RsDigitalKontaktdata())
                .medl(new RsMedl())
                .udistub(new RsUdiPerson())
                .inntektstub(InntektMultiplierWrapper.builder()
                        .inntektsinformasjon(List.of(new RsInntektsinformasjon()))
                        .build())
                .arenaforvalter(new Arenadata())
                .inntektsmelding(new RsInntektsmelding())
                .brregstub(new RsBregdata())
                .histark(RsHistark.builder()
                        .dokumenter(List.of(new RsHistark.RsHistarkDokument()))
                        .build())
                .sykemelding(RsSykemelding.builder()
                        .nySykemelding(new RsSykemelding.RsNySykemelding())
                        .build())
                .skattekort(SkattekortRequestDTO.builder()
                        .arbeidsgiverSkatt(List.of(new ArbeidstakerSkatt()))
                        .build())
                .nomdata(new RsNomData())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestillingMedMiljoer());

        assertThat(result, hasItem(SystemTyper.KRRSTUB));
        assertThat(result, hasItem(SystemTyper.MEDL));
        assertThat(result, hasItem(SystemTyper.UDISTUB));
        assertThat(result, hasItem(SystemTyper.INNTK));
        assertThat(result, hasItem(SystemTyper.ARENA_BRUKER));
        assertThat(result, hasItem(SystemTyper.INNTKMELD));
        assertThat(result, hasItem(SystemTyper.BRREGSTUB));
        assertThat(result, hasItem(SystemTyper.HISTARK));
        assertThat(result, hasItem(SystemTyper.SYKEMELDING));
        assertThat(result, hasItem(SystemTyper.SKATTEKORT));
        assertThat(result, hasItem(SystemTyper.NOM));
    }

    @Test
    void shouldNotResolveFagsystemerWithoutContent() {

        var kriterier = BestilteKriterier.builder()
                .inntektstub(new InntektMultiplierWrapper())
                .sykemelding(new RsSykemelding())
                .skattekort(new SkattekortRequestDTO())
                .henvendelse(new RsHenvendelse())
                .histark(new RsHistark())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestillingMedMiljoer());

        assertThat(result, not(hasItem(SystemTyper.INNTK)));
        assertThat(result, not(hasItem(SystemTyper.SYKEMELDING)));
        assertThat(result, not(hasItem(SystemTyper.SKATTEKORT)));
        assertThat(result, not(hasItem(SystemTyper.HENVENDELSE)));
        assertThat(result, not(hasItem(SystemTyper.HISTARK)));
    }

    @Test
    void shouldNotResolveMiljoeAvhengigeFagsystemerWithoutMiljoer() {

        var kriterier = BestilteKriterier.builder()
                .aareg(List.of(rsAaregMedArbeidsgiver()))
                .instdata(List.of(new RsInstdata()))
                .dokarkiv(List.of(new RsDokarkiv()))
                .arenaforvalter(new Arenadata())
                .inntektsmelding(new RsInntektsmelding())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, not(hasItem(SystemTyper.AAREG)));
        assertThat(result, not(hasItem(SystemTyper.INST2)));
        assertThat(result, not(hasItem(SystemTyper.DOKARKIV)));
        assertThat(result, not(hasItem(SystemTyper.ARENA_BRUKER)));
        assertThat(result, not(hasItem(SystemTyper.INNTKMELD)));
    }

    @Test
    void shouldNotResolveAaregForUnsupportedMiljoe() {

        var kriterier = BestilteKriterier.builder()
                .aareg(List.of(rsAaregMedArbeidsgiver()))
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, Bestilling.builder().miljoer("q0").build());

        assertThat(result, not(hasItem(SystemTyper.AAREG)));
    }

    @Test
    void shouldNotResolveAaregWithoutArbeidsgiver() {

        var kriterier = BestilteKriterier.builder()
                .aareg(List.of(new RsAareg()))
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestillingMedMiljoer());

        assertThat(result, not(hasItem(SystemTyper.AAREG)));
    }

    @Test
    void shouldResolveInntkForInntekter() {

        var kriterier = BestilteKriterier.builder()
                .inntekter(List.of(new RsInntekter()))
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.INNTK));
    }

    @Test
    void shouldResolveHenvendelseForMeldinger() {

        var kriterier = BestilteKriterier.builder()
                .henvendelse(RsHenvendelse.builder()
                        .meldinger(List.of(new RsHenvendelse.Melding()))
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.HENVENDELSE));
    }

    private static RsAareg rsAaregMedArbeidsgiver() {

        return RsAareg.builder()
                .arbeidsgiver(new RsOrganisasjon())
                .build();
    }

    @Test
    void shouldResolveListBasedFagsystemer() {

        var kriterier = BestilteKriterier.builder()
                .aareg(List.of(rsAaregMedArbeidsgiver()))
                .fullmakt(List.of(new RsFullmakt()))
                .instdata(List.of(new RsInstdata()))
                .dokarkiv(List.of(new RsDokarkiv()))
                .yrkesskader(List.of(new YrkesskadeRequest()))
                .etterlatteYtelser(List.of(new EtterlatteYtelse()))
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestillingMedMiljoer());

        assertThat(result, hasItem(SystemTyper.AAREG));
        assertThat(result, hasItem(SystemTyper.FULLMAKT));
        assertThat(result, hasItem(SystemTyper.INST2));
        assertThat(result, hasItem(SystemTyper.DOKARKIV));
        assertThat(result, hasItem(SystemTyper.YRKESSKADE));
        assertThat(result, hasItem(SystemTyper.ETTERLATTE));
    }

    @Test
    void shouldNotResolveListBasedFagsystemerWhenEmpty() {

        var kriterier = BestilteKriterier.builder()
                .aareg(List.of())
                .fullmakt(List.of())
                .instdata(List.of())
                .dokarkiv(List.of())
                .yrkesskader(List.of())
                .etterlatteYtelser(List.of())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, not(hasItem(SystemTyper.AAREG)));
        assertThat(result, not(hasItem(SystemTyper.FULLMAKT)));
        assertThat(result, not(hasItem(SystemTyper.INST2)));
        assertThat(result, not(hasItem(SystemTyper.DOKARKIV)));
        assertThat(result, not(hasItem(SystemTyper.YRKESSKADE)));
        assertThat(result, not(hasItem(SystemTyper.ETTERLATTE)));
    }

    @Test
    void shouldResolveBankkontoToKontoregisterAndTpsMessaging() {

        var kriterier = BestilteKriterier.builder()
                .bankkonto(BankkontoData.builder()
                        .norskBankkonto(new BankkontonrNorskDTO())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.KONTOREGISTER));
        assertThat(result, hasItem(SystemTyper.TPS_MESSAGING));
    }

    @Test
    void shouldResolveTpsMessagingForUtenlandskBankkonto() {

        var kriterier = BestilteKriterier.builder()
                .bankkonto(BankkontoData.builder()
                        .utenlandskBankkonto(new BankkontonrUtlandDTO())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.TPS_MESSAGING));
    }

    @Test
    void shouldNotResolveKontoregisterOrTpsMessagingForBankkontoWithoutKonto() {

        var kriterier = BestilteKriterier.builder()
                .bankkonto(new BankkontoData())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, not(hasItem(SystemTyper.KONTOREGISTER)));
        assertThat(result, not(hasItem(SystemTyper.TPS_MESSAGING)));
    }

    @Test
    void shouldResolveKontoregisterForBankkontoFromTpsMessaging() {

        var kriterier = BestilteKriterier.builder()
                .bankkonto(new BankkontoData())
                .tpsMessaging(RsTpsMessaging.builder()
                        .norskBankkonto(new BankkontonrNorskDTO())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.KONTOREGISTER));
    }

    @Test
    void shouldResolveTpsMessagingForSkjermingWithDates() {

        var kriterier = BestilteKriterier.builder()
                .skjerming(RsSkjerming.builder()
                        .egenAnsattDatoFom(LocalDateTime.now())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.TPS_MESSAGING));
    }

    @Test
    void shouldResolveTpsMessagingForTpsMessagingEgenansatt() {

        var kriterier = BestilteKriterier.builder()
                .tpsMessaging(RsTpsMessaging.builder()
                        .egenAnsattDatoTom(LocalDate.now())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.TPS_MESSAGING));
    }

    @Test
    void shouldNotResolveTpsMessagingForSkjermingWithoutDates() {

        var kriterier = BestilteKriterier.builder()
                .skjerming(RsSkjerming.builder().build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, not(hasItem(SystemTyper.TPS_MESSAGING)));
    }

    @Test
    void shouldNotResolveTpsMessagingForPdldataWithPerson() {

        var pdldata = new PdlPersondata();
        pdldata.setPerson(new PersonDTO());

        var kriterier = BestilteKriterier.builder()
                .pdldata(pdldata)
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, not(hasItem(SystemTyper.TPS_MESSAGING)));
    }

    @Test
    void shouldResolveSkjermingsregisterForSkjermingWithDates() {

        var kriterier = BestilteKriterier.builder()
                .skjerming(RsSkjerming.builder()
                        .egenAnsattDatoFom(LocalDateTime.now())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.SKJERMINGSREGISTER));
    }

    @Test
    void shouldResolveSkjermingsregisterForTpsMessagingEgenansatt() {

        var kriterier = BestilteKriterier.builder()
                .tpsMessaging(RsTpsMessaging.builder()
                        .egenAnsattDatoFom(LocalDate.now())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.SKJERMINGSREGISTER));
    }

    @Test
    void shouldNotResolveSkjermingsregisterForSkjermingWithoutDates() {

        var kriterier = BestilteKriterier.builder()
                .skjerming(RsSkjerming.builder().build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, not(hasItem(SystemTyper.SKJERMINGSREGISTER)));
    }

    @Test
    void shouldResolveTypicalImportScenario() {

        var kriterier = BestilteKriterier.builder()
                .pdldata(pdldataMedPerson())
                .bankkonto(BankkontoData.builder()
                        .norskBankkonto(new BankkontonrNorskDTO())
                        .build())
                .skjerming(RsSkjerming.builder()
                        .egenAnsattDatoFom(LocalDateTime.now())
                        .build())
                .pensjonforvalter(PensjonData.builder()
                        .inntekt(new PensjonData.PoppInntekt())
                        .build())
                .aareg(List.of(rsAaregMedArbeidsgiver()))
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier,
                Bestilling.builder().pdlImport(FNR).miljoer("q1").build());

        assertThat(result, hasItem(SystemTyper.PDLIMPORT));
        assertThat(result, hasItem(SystemTyper.PDL_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PDL_ORDRE));
        assertThat(result, hasItem(SystemTyper.PEN_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PEN_INNTEKT));
        assertThat(result, hasItem(SystemTyper.AAREG));
        assertThat(result, hasItem(SystemTyper.KONTOREGISTER));
        assertThat(result, hasItem(SystemTyper.TPS_MESSAGING));
        assertThat(result, hasItem(SystemTyper.SKJERMINGSREGISTER));
    }

    @Test
    void shouldNotContainDuplicates() {

        var pdldata = new PdlPersondata();
        pdldata.setPerson(new PersonDTO());

        var kriterier = BestilteKriterier.builder()
                .pdldata(pdldata)
                .bankkonto(new BankkontoData())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, importBestilling());

        var uniqueCount = result.stream().distinct().count();
        assertThat((long) result.size(), is(uniqueCount));
    }

    @Test
    void shouldNotIncludePdlImportForGjenopprett() {

        var result = UtledFagsystemUtil.resolve(BestilteKriterier.builder().build(),
                Bestilling.builder().opprettetFraId(1L).build());

        assertThat(result, not(hasItem(SystemTyper.PDLIMPORT)));
        assertThat(result, not(hasItem(SystemTyper.PDL_FORVALTER)));
        assertThat(result, hasItem(SystemTyper.PDL_ORDRE));
        assertThat(result, hasItem(SystemTyper.PDL_PERSONSTATUS));
    }

    @Test
    void shouldNotResolvePdlOrdreForGjenopprettTestnorgeIdent() {

        var result = UtledFagsystemUtil.resolve(BestilteKriterier.builder().build(),
                Bestilling.builder().ident(TESTNORGE_FNR).gjenopprettetFraIdent(TESTNORGE_FNR).build());

        assertThat(result, not(hasItem(SystemTyper.PDL_ORDRE)));
        assertThat(result, not(hasItem(SystemTyper.PDL_FORVALTER)));
        assertThat(result, hasItem(SystemTyper.PDL_PERSONSTATUS));
    }
}
