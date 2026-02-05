package no.nav.dolly.mapper.strategy;

import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.skattekort.domain.ArbeidsgiverSkatt;
import no.nav.dolly.bestilling.skattekort.domain.Forskuddstrekk;
import no.nav.dolly.bestilling.skattekort.domain.Frikort;
import no.nav.dolly.bestilling.skattekort.domain.IdentifikatorForEnhetEllerPerson;
import no.nav.dolly.bestilling.skattekort.domain.Skattekort;
import no.nav.dolly.bestilling.skattekort.domain.Skattekortmelding;
import no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning;
import no.nav.dolly.bestilling.skattekort.domain.Trekkprosent;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.RsNomData;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.arbeidssoekerregistrering.RsArbeidssoekerregisteret;
import no.nav.dolly.domain.resultset.arbeidssoekerregistrering.RsArbeidssoekerregisteret.JobbsituasjonDetaljer;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukertype;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap115;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaDagpenger;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.breg.RsBregdata.PersonRolle;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.etterlatte.EtterlatteYtelse;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding.Inntektsmelding;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding.RsArbeidsgiver;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding.RsKontaktinformasjon;
import no.nav.dolly.domain.resultset.inntektstub.Inntekt;
import no.nav.dolly.domain.resultset.inntektstub.Inntekt.InntektType;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.domain.resultset.inst.InstdataInstitusjonstype;
import no.nav.dolly.domain.resultset.inst.InstdataKategori;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.medl.RsMedl;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.pensjon.PensjonData.AfpOffentlig;
import no.nav.dolly.domain.resultset.pensjon.PensjonData.Alderspensjon;
import no.nav.dolly.domain.resultset.pensjon.PensjonData.AlderspensjonNyUtaksgrad;
import no.nav.dolly.domain.resultset.pensjon.PensjonData.Pensjonsavtale;
import no.nav.dolly.domain.resultset.pensjon.PensjonData.Pensjonsavtale.AvtaleKategori;
import no.nav.dolly.domain.resultset.pensjon.PensjonData.PoppGenerertInntekt;
import no.nav.dolly.domain.resultset.pensjon.PensjonData.TpOrdning;
import no.nav.dolly.domain.resultset.pensjon.PensjonData.TpYtelse;
import no.nav.dolly.domain.resultset.pensjon.PensjonData.TpYtelseType;
import no.nav.dolly.domain.resultset.pensjon.PensjonData.Uforetrygd;
import no.nav.dolly.domain.resultset.sigrunstub.KodeverknavnGrunnlag;
import no.nav.dolly.domain.resultset.sigrunstub.RsLignetInntekt;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.domain.resultset.sigrunstub.RsSummertSkattegrunnlag;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding.RsDetaljertSykemelding.DollyDiagnose;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.opphold.RsUdiOppholdStatus;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdsrettType;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.yrkesskade.v1.FerdigstillSak;
import no.nav.testnav.libs.dto.yrkesskade.v1.InnmelderRolletype;
import no.nav.testnav.libs.dto.yrkesskade.v1.Klassifisering;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class DollyRequest2MalBestillingMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new DollyRequest2MalBestillingMappingStrategy());
    }

    @Test
    void shouldAccuulateEnvironments() {

        var target = mapperFacade.map(RsDollyUtvidetBestilling.builder()
                .environments(Set.of("Q1", "Q4"))
                .build(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(RsDollyUtvidetBestilling.builder()
                .environments(Set.of("Q1", "Q2"))
                .build(), target);

        assertThat(target.getEnvironments(), hasItems("Q1", "Q2", "Q4"));
    }

    @Test
    void shouldAccumulateEtterlatteYtelser() {

        var target = mapperFacade.map(RsDollyUtvidetBestilling.builder()
                .etterlatteYtelser(List.of(EtterlatteYtelse.builder()
                        .soeker("12345678901")
                        .ytelse(EtterlatteYtelse.YtelseType.OMSTILLINGSSTOENAD)
                        .build()))
                .build(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(RsDollyUtvidetBestilling.builder()
                .etterlatteYtelser(List.of(EtterlatteYtelse.builder()
                        .soeker("10987654321")
                        .ytelse(EtterlatteYtelse.YtelseType.BARNEPENSJON)
                        .build()))
                .build(), target);

        assertThat(target.getEtterlatteYtelser(), hasItems(
                EtterlatteYtelse.builder()
                        .soeker("12345678901")
                        .ytelse(EtterlatteYtelse.YtelseType.OMSTILLINGSSTOENAD)
                        .build(),
                EtterlatteYtelse.builder()
                        .soeker("10987654321")
                        .ytelse(EtterlatteYtelse.YtelseType.BARNEPENSJON)
                        .build()));
    }

    @Test
    void shouldAccumulateHistark() {

        var target = mapperFacade.map(buildHistark1(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildHistark2(), target);

        assertThat(target.getHistark().getDokumenter(), hasItems(
                RsHistark.RsHistarkDokument.builder()
                        .antallSider(12)
                        .dokumentReferanse(2323123L)
                        .tittel("Utenlandsk")
                        .enhetsnavn("Enhet 1")
                        .skanner("Skanner 1")
                        .build(),
                RsHistark.RsHistarkDokument.builder()
                        .antallSider(20)
                        .dokumentReferanse(9876543L)
                        .tittel("Norsk")
                        .enhetsnavn("Enhet 2")
                        .skanner("Skanner 2")
                        .build()));
    }

    @Test
    void shouldAccumulateDokarkiv() {

        var target = mapperFacade.map(buildDokarkiv1(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildDokarkiv2(), target);

        assertThat(target.getDokarkiv(), hasItems(
                RsDokarkiv.builder()
                        .tema("TEST")
                        .sak(RsDokarkiv.Sak.builder()
                                .fagsakId("123456")
                                .build())
                        .kanal("TEST")
                        .dokumenter(List.of(RsDokarkiv.Dokument.builder()
                                .brevkode("TEST1")
                                .tittel("Dokument 1")
                                .dokumentvarianter(List.of(RsDokarkiv.Dokument.DokumentVariant.builder()
                                        .dokumentReferanse(1234L)
                                        .filtype("PDF")
                                        .variantformat("ARKIV")
                                        .dokumentReferanse(7856L)
                                        .build()))
                                .build()))
                        .build(),
                RsDokarkiv.builder()
                        .tema("TEST2")
                        .sak(RsDokarkiv.Sak.builder()
                                .fagsakId("3456122")
                                .build())
                        .kanal("TEST2")
                        .dokumenter(List.of(RsDokarkiv.Dokument.builder()
                                .brevkode("TEST2")
                                .tittel("Dokument 2")
                                .dokumentvarianter(List.of(RsDokarkiv.Dokument.DokumentVariant.builder()
                                        .dokumentReferanse(6757L)
                                        .filtype("DOC")
                                        .variantformat("ORIGINAL")
                                        .dokumentReferanse(8890L)
                                        .build()))
                                .build()))
                        .build()));
    }

    @Test
    void shouldAccumulateYrkesskader() {

        var target = mapperFacade.map(buildYrkesskader1(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildYrkesskader2(), target);

        assertThat(target.getYrkesskader(), hasItems(
                YrkesskadeRequest.builder()
                        .ferdigstillSak(FerdigstillSak.AVSLAG)
                        .innmelderrolle(InnmelderRolletype.denSkadelidte)
                        .klassifisering(Klassifisering.BAGATELLMESSIGE_SKADER)
                        .build(),
                YrkesskadeRequest.builder()
                        .ferdigstillSak(FerdigstillSak.GODKJENT)
                        .innmelderrolle(InnmelderRolletype.vergeOgForesatt)
                        .klassifisering(Klassifisering.MELLOMSKADER)
                        .build()));
    }

    @Test
    void shouldAccumulateInstdata() {

        var target = mapperFacade.map(buildInstdata1(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildInstdata2(), target);

        assertThat(target.getInstdata(), hasItems(
                RsInstdata.builder()
                        .institusjonstype(InstdataInstitusjonstype.FO)
                        .kategori(InstdataKategori.F)
                        .startdato(LocalDate.of(2023, 1, 1).atStartOfDay())
                        .build(),
                RsInstdata.builder()
                        .institusjonstype(InstdataInstitusjonstype.AS)
                        .kategori(InstdataKategori.A)
                        .startdato(LocalDate.of(2025, 1, 1).atStartOfDay())
                        .build()));
    }

    @Test
    void shouldAccumulateSigrunstub() {
        var target = mapperFacade.map(buildSigrunstub1(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildSigrunstub2(), target);

        assertThat(target.getSigrunstub(), hasItems(
                RsLignetInntekt.builder()
                        .inntektsaar("2023")
                        .grunnlag(List.of(KodeverknavnGrunnlag.builder()
                                .tekniskNavn("GRUNNLAG_1")
                                .verdi("GRUNNLAG_2")
                                .build()))
                        .build(),
                RsLignetInntekt.builder()
                        .inntektsaar("2025")
                        .grunnlag(List.of(KodeverknavnGrunnlag.builder()
                                .tekniskNavn("GRUNNLAG_3")
                                .verdi("GRUNNLAG_4")
                                .build()))
                        .build()));
    }

    @Test
    void shouldAccumulateSigrunstubPensjonsgivende() {

        var target = mapperFacade.map(buildSigrunstub1(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildSigrunstub2(), target);


        assertThat(target.getSigrunstubPensjonsgivende(), hasItems(
                RsPensjonsgivendeForFolketrygden.builder()
                        .inntektsaar("2023")
                        .pensjonsgivendeInntekt(List.of(Map.of("key", "value")))
                        .build(),
                RsPensjonsgivendeForFolketrygden.builder()
                        .inntektsaar("2025")
                        .pensjonsgivendeInntekt(List.of(Map.of("key", "value")))
                        .build()));
    }

    @Test
    void shouldAccumulateSigrunstubSummertSkattegrunnlag() {

        var target = mapperFacade.map(buildSigrunstub1(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildSigrunstub2(), target);

        assertThat(target.getSigrunstubSummertSkattegrunnlag(), hasItems(
                RsSummertSkattegrunnlag.builder()
                        .inntektsaar("2023")
                        .grunnlag(List.of(RsSummertSkattegrunnlag.Grunnlag.builder()
                                .kategori("KATEGORI_1")
                                .beloep(2000)
                                .tekniskNavn("TEKNISK_NAVN_1")
                                .andelOverfoertFraBarn(50)
                                .build()))
                        .build(),
                RsSummertSkattegrunnlag.builder()
                        .inntektsaar("2025")
                        .grunnlag(List.of(RsSummertSkattegrunnlag.Grunnlag.builder()
                                .kategori("KATEGORI_1")
                                .beloep(2000)
                                .tekniskNavn("TEKNISK_NAVN_1")
                                .andelOverfoertFraBarn(50)
                                .build()))
                        .build()));
    }

    @Test
    void shouldAccumulateAaregService() {

        var target = mapperFacade.map(buildAareg1(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildAareg2(), target);

        assertThat(target.getAareg(), hasItem(RsAareg.builder()
                .arbeidsgiver(RsOrganisasjon.builder()
                        .aktoertype("ORG")
                        .orgnummer("123456789")
                        .build())
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .ansettelsesform("FAST")
                        .yrke("UTVIKLER")
                        .avloenningstype("MAANEDSLONN")
                        .stillingsprosent(100.0)
                        .build())
                .build()));
        assertThat(target.getAareg(), hasItem(RsAareg.builder()
                .arbeidsgiver(RsAktoerPerson.builder()
                        .aktoertype("PERS")
                        .ident("12345678901")
                        .build())
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .ansettelsesform("LØST")
                        .yrke("SAUEPASSER")
                        .avloenningstype("BETALING I ULL")
                        .stillingsprosent(50.0)
                        .build())
                .build()));
    }

    @Test
    void shouldAccumulateSkattekort() {

        var target = mapperFacade.map(buildSkattekort1(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildSkattekort2(), target);

        assertThat(target.getSkattekort().getArbeidsgiverSkatt(), hasItem(
                ArbeidsgiverSkatt.builder()
                        .arbeidsgiveridentifikator(IdentifikatorForEnhetEllerPerson.builder()
                                .organisasjonsnummer("123456789")
                                .build())
                        .arbeidstaker(List.of(Skattekortmelding.builder()
                                .arbeidstakeridentifikator("10987654321")
                                .skattekort(Skattekort.builder()
                                        .forskuddstrekk(List.of(Forskuddstrekk.builder()
                                                .frikort(Frikort.builder()
                                                        .frikortbeloep(50000)
                                                        .build())
                                                .build()))
                                        .build())
                                .inntektsaar(2025)
                                .tilleggsopplysning(List.of(Tilleggsopplysning.KILDESKATT_PAA_PENSJON))
                                .build()))
                        .build()));
        assertThat(target.getSkattekort().getArbeidsgiverSkatt(), hasItem(ArbeidsgiverSkatt.builder()
                .arbeidsgiveridentifikator(IdentifikatorForEnhetEllerPerson.builder()
                        .organisasjonsnummer("987654321")
                        .build())
                .arbeidstaker(List.of(Skattekortmelding.builder()
                        .arbeidstakeridentifikator("12345678901")
                        .skattekort(Skattekort.builder()
                                .forskuddstrekk(List.of(Forskuddstrekk.builder()
                                        .trekkprosent(Trekkprosent.builder()
                                                .prosentsats(28)
                                                .build())
                                        .build()))
                                .build())
                        .inntektsaar(2024)
                        .tilleggsopplysning(List.of(Tilleggsopplysning.KILDESKATT_PAA_LOENN))
                        .build()))
                .build()));
    }

    @Test
    void shouldAccumulateArbeidsplassen() {

        var target = mapperFacade.map(buildNavCV1(), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildNavCV2(), target);

        assertThat(target.getArbeidsplassenCV().getSistEndretAvNav(), is(equalTo((true))));
        assertThat(target.getArbeidsplassenCV().getHarBil(), is(equalTo((true))));
        assertThat(target.getArbeidsplassenCV().getSammendrag(), is(equalTo(("Sammendrag"))));
        assertThat(target.getArbeidsplassenCV().getSistEndret(), is(equalTo((LocalDate.of(2025, 10, 10).atStartOfDay()))));
        assertThat(target.getArbeidsplassenCV().getAndreGodkjenninger(), hasItems(ArbeidsplassenCVDTO.AnnenGodkjenning.builder()
                .certificateName("Nav sertifikat")
                .build(), ArbeidsplassenCVDTO.AnnenGodkjenning.builder()
                .certificateName("Veritas sertifikat")
                .build()));
        assertThat(target.getArbeidsplassenCV().getAnnenErfaring(), hasItems(ArbeidsplassenCVDTO.AnnenErfaring.builder()
                .description("Gjete sauer")
                .build(), ArbeidsplassenCVDTO.AnnenErfaring.builder()
                .description("Gjete gjeiter")
                .build()));
        assertThat(target.getArbeidsplassenCV().getArbeidserfaring(), hasItems(ArbeidsplassenCVDTO.Arbeidserfaring.builder()
                .description("Utvikler hos NAV")
                .build(), ArbeidsplassenCVDTO.Arbeidserfaring.builder()
                .description("Sauepassing")
                .build()));
        assertThat(target.getArbeidsplassenCV().getFagbrev(), hasItems(ArbeidsplassenCVDTO.Fagbrev.builder()
                .type(ArbeidsplassenCVDTO.VocationalCertification.SVENNEBREV_FAGBREV)
                .build(), ArbeidsplassenCVDTO.Fagbrev.builder()
                .type(ArbeidsplassenCVDTO.VocationalCertification.AUTORISASJON)
                .build()));
        assertThat(target.getArbeidsplassenCV().getFoererkort(), hasItems(ArbeidsplassenCVDTO.Foererkort.builder()
                .type("klasse B")
                .build(), ArbeidsplassenCVDTO.Foererkort.builder()
                .type("klasse A")
                .build()));
        assertThat(target.getArbeidsplassenCV().getKompetanser(), hasItems(ArbeidsplassenCVDTO.Kompetanse.builder()
                .title("Java")
                .build(), ArbeidsplassenCVDTO.Kompetanse.builder()
                .title("Python")
                .build()));
        assertThat(target.getArbeidsplassenCV().getKurs(), hasItems(ArbeidsplassenCVDTO.Kurs.builder()
                .title("Sveising")
                .build(), ArbeidsplassenCVDTO.Kurs.builder()
                .title("Hagestell")
                .build()));
        assertThat(target.getArbeidsplassenCV().getOffentligeGodkjenninger(), hasItems(ArbeidsplassenCVDTO.OffentligeGodkjenning.builder()
                .title("Sikkerhetsklarering")
                .build(), ArbeidsplassenCVDTO.OffentligeGodkjenning.builder()
                .title("Førerkort")
                .build()));
        assertThat(target.getArbeidsplassenCV().getSpraak(), hasItems(ArbeidsplassenCVDTO.Spraak.builder()
                .language("Fremmedlandsk")
                .build(), ArbeidsplassenCVDTO.Spraak.builder()
                .language("Utenlandsk")
                .build()));
        assertThat(target.getArbeidsplassenCV().getUtdanning(), hasItems(ArbeidsplassenCVDTO.Utdanning.builder()
                .description("Bachelor i noe")
                .build(), ArbeidsplassenCVDTO.Utdanning.builder()
                .description("Bachelor i noe annet")
                .build()));
    }

    @Test
    void shouldCopyUdistub() {

        var bestilling = RsDollyUtvidetBestilling.builder()
                .udistub(RsUdiPerson.builder()
                        .flyktning(true)
                        .harOppholdsTillatelse(true)
                        .oppholdStatus(RsUdiOppholdStatus.builder()
                                .eosEllerEFTABeslutningOmOppholdsrett(UdiOppholdsrettType.FAMILIE)
                                .build())
                        .build())
                .build();

        var target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getUdistub().getFlyktning(), is(equalTo(true)));
        assertThat(target.getUdistub().getHarOppholdsTillatelse(), is(equalTo(true)));
        assertThat(target.getUdistub().getOppholdStatus().getEosEllerEFTABeslutningOmOppholdsrett(), is(equalTo(UdiOppholdsrettType.FAMILIE)));
    }

    @Test
    void shouldCopySykemelding() {

        var bestilling = RsDollyUtvidetBestilling.builder()
                .sykemelding(RsSykemelding.builder()
                        .detaljertSykemelding(RsSykemelding.RsDetaljertSykemelding.builder()
                                .arbeidsgiver(RsSykemelding.RsDetaljertSykemelding.Arbeidsgiver.builder()
                                        .navn("Arbeidsgiver AS")
                                        .build())
                                .hovedDiagnose(DollyDiagnose.builder()
                                        .diagnosekode("A12B")
                                        .build())
                                .helsepersonell(RsSykemelding.RsDetaljertSykemelding.Helsepersonell.builder()
                                        .samhandlerType("FASTLEGE")
                                        .build())
                                .build())
                        .build())
                .build();

        var target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getSykemelding().getDetaljertSykemelding().getArbeidsgiver().getNavn(), is(equalTo("Arbeidsgiver AS")));
        assertThat(target.getSykemelding().getDetaljertSykemelding().getHovedDiagnose().getDiagnosekode(), is(equalTo("A12B")));
        assertThat(target.getSykemelding().getDetaljertSykemelding().getHelsepersonell().getSamhandlerType(), is(equalTo("FASTLEGE")));
    }

    @Test
    void shouldCopyArbeidssoekerregisteret() {

        var bestilling = RsDollyUtvidetBestilling.builder()
                .arbeidssoekerregisteret(RsArbeidssoekerregisteret.builder()
                        .jobbsituasjonsdetaljer(JobbsituasjonDetaljer.builder()
                                .stillingstittel("Utvikler")
                                .build())
                        .utdanningGodkjent(true)
                        .build())
                .build();

        var target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getArbeidssoekerregisteret().getJobbsituasjonsdetaljer().getStillingstittel(), is(equalTo("Utvikler")));
        assertThat(target.getArbeidssoekerregisteret().getUtdanningGodkjent(), is(equalTo(true)));
    }

    @Test
    void shouldCopyBankkonto() {

        var bestilling = RsDollyUtvidetBestilling.builder()
                .bankkonto(BankkontoData.builder()
                        .norskBankkonto(BankkontonrNorskDTO.builder()
                                .kontonummer("12312312232")
                                .tilfeldigKontonummer(false)
                                .build())
                        .utenlandskBankkonto(BankkontonrUtlandDTO.builder()
                                .kontonummer("3463464646644646")
                                .banknavn("Utenlandsk Bank")
                                .build())
                        .build())
                .build();

        var target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getBankkonto().getNorskBankkonto().getKontonummer(), is(equalTo("12312312232")));
        assertThat(target.getBankkonto().getNorskBankkonto().getTilfeldigKontonummer(), is(equalTo(false)));
        assertThat(target.getBankkonto().getUtenlandskBankkonto().getKontonummer(), is(equalTo("3463464646644646")));
        assertThat(target.getBankkonto().getUtenlandskBankkonto().getBanknavn(), is(equalTo("Utenlandsk Bank")));
    }

    @Test
    void shouldCopyBrregstub() {

        val bestilling = RsDollyUtvidetBestilling.builder()
                .brregstub(RsBregdata.builder()
                        .enheter(List.of(RsBregdata.RolleTo.builder()
                                .foretaksNavn(RsBregdata.NavnTo.builder()
                                        .navn1("Fåretak")
                                        .build())
                                .personroller(List.of(PersonRolle.builder()
                                        .egenskap(RsBregdata.Egenskap.DELTAGER)
                                        .build()))
                                .build()))
                        .build())
                .build();

        val target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getBrregstub().getEnheter(), hasItem(RsBregdata.RolleTo.builder()
                .foretaksNavn(RsBregdata.NavnTo.builder()
                        .navn1("Fåretak")
                        .build())
                .personroller(List.of(PersonRolle.builder()
                        .egenskap(RsBregdata.Egenskap.DELTAGER)
                        .build()))
                .build()));
    }

    @Test
    void shouldCopyFullmakt() {

        var target = mapperFacade.map(buildFullmakt("3123123", "123123132"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildFullmakt("12345678901", "10987654321"), target);

        assertThat(target.getFullmakt(), hasItem(RsFullmakt.builder()
                .fullmaktsgiver("12345678901")
                .fullmektig("10987654321")
                .build()));
        assertThat(target.getFullmakt(), hasItem(RsFullmakt.builder()
                .fullmaktsgiver("3123123")
                .fullmektig("123123132")
                .build()));
    }

    @Test
    void shouldCopySkjerming() {

        var bestilling = RsDollyUtvidetBestilling.builder()
                .skjerming(RsSkjerming.builder()
                        .egenAnsattDatoFom(LocalDate.of(2024, 1, 1).atStartOfDay())
                        .egenAnsattDatoTom(LocalDate.of(2025, 12, 31).atStartOfDay())
                        .build())
                .build();

        var target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getSkjerming().getEgenAnsattDatoFom(), is(equalTo(LocalDate.of(2024, 1, 1).atStartOfDay())));
        assertThat(target.getSkjerming().getEgenAnsattDatoTom(), is(equalTo(LocalDate.of(2025, 12, 31).atStartOfDay())));
    }

    @Test
    void shouldMergePensjonAfpOffentligOgAlderspensjon() {

        val bestillingAfpOffentlig = RsDollyUtvidetBestilling.builder()
                .pensjonforvalter(PensjonData.builder()
                        .afpOffentlig(AfpOffentlig.builder()
                                .direktekall(List.of("TEST_AFP_OFFENTLIG_1", "TEST_AFP_OFFENTLIG_2"))
                                .build())
                        .build())
                .build();

        val bestillingAlderspensjon = RsDollyUtvidetBestilling.builder()
                .pensjonforvalter(PensjonData.builder()
                        .alderspensjon(Alderspensjon.builder()
                                .uttaksgrad(100)
                                .inkluderAfpPrivat(true)
                                .build())
                        .build())
                .build();

        var target = mapperFacade.map(bestillingAfpOffentlig, RsDollyUtvidetBestilling.class);
        mapperFacade.map(bestillingAlderspensjon, target);

        assertThat(target.getPensjonforvalter().getAfpOffentlig().getDirektekall(), hasItems("TEST_AFP_OFFENTLIG_1", "TEST_AFP_OFFENTLIG_2"));
        assertThat(target.getPensjonforvalter().getAlderspensjon().getUttaksgrad(), is(equalTo(100)));
        assertThat(target.getPensjonforvalter().getAlderspensjon().getInkluderAfpPrivat(), is(equalTo(true)));
    }

    @Test
    void shouldMergePensjonAlderspensjonNyUttaksgradOgInntekt() {

        val bestillingAlderspensjonNyUttaksgrad = RsDollyUtvidetBestilling.builder()
                .pensjonforvalter(PensjonData.builder()
                        .alderspensjonNyUtaksgrad(AlderspensjonNyUtaksgrad.builder()
                                .nyUttaksgrad(80)
                                .navEnhetId("1024")
                                .build())
                        .build())
                .build();

        val bestillingInntekt = RsDollyUtvidetBestilling.builder()
                .pensjonforvalter(PensjonData.builder()
                        .inntekt(PensjonData.PoppInntekt.builder()
                                .belop(100000)
                                .fomAar(1975)
                                .tomAar(2025)
                                .build())
                        .build())
                .build();

        var target = mapperFacade.map(bestillingAlderspensjonNyUttaksgrad, RsDollyUtvidetBestilling.class);
        mapperFacade.map(bestillingInntekt, target);

        assertThat(target.getPensjonforvalter().getAlderspensjonNyUtaksgrad().getNyUttaksgrad(), is(equalTo(80)));
        assertThat(target.getPensjonforvalter().getAlderspensjonNyUtaksgrad().getNavEnhetId(), is(equalTo("1024")));
        assertThat(target.getPensjonforvalter().getInntekt().getBelop(), is(equalTo(100000)));
        assertThat(target.getPensjonforvalter().getInntekt().getFomAar(), is(equalTo(1975)));
        assertThat(target.getPensjonforvalter().getInntekt().getTomAar(), is(equalTo(2025)));
    }

    @Test
    void shouldMergePensjonGenerertInntektOgUfoeretrygd() {

        val bestillingAfpPrivat = RsDollyUtvidetBestilling.builder()
                .pensjonforvalter(PensjonData.builder()
                        .generertInntekt(PensjonData.PoppGenerertInntektWrapper.builder()
                                .inntekter(List.of(PoppGenerertInntekt.builder()
                                        .ar(2025)
                                        .inntekt(150000)
                                        .build()))
                                .build())
                        .build())
                .build();

        val bestillingUfoere = RsDollyUtvidetBestilling.builder()
                .pensjonforvalter(PensjonData.builder()
                        .uforetrygd(Uforetrygd.builder()
                                .inntektEtterUforhet(13232)
                                .navEnhetId("2048")
                                .build())
                        .build())
                .build();

        var target = mapperFacade.map(bestillingAfpPrivat, RsDollyUtvidetBestilling.class);
        mapperFacade.map(bestillingUfoere, target);

        assertThat(target.getPensjonforvalter().getGenerertInntekt().getInntekter(), hasItem(
                PoppGenerertInntekt.builder()
                        .ar(2025)
                        .inntekt(150000)
                        .build()));
        assertThat(target.getPensjonforvalter().getUforetrygd().getInntektEtterUforhet(), is(equalTo(13232)));
        assertThat(target.getPensjonforvalter().getUforetrygd().getNavEnhetId(), is(equalTo("2048")));
    }

    @Test
    void shouldAccumulatePensjonTP() {

        var target = mapperFacade.map(buildPensjonTP("Ordning 1", TpYtelseType.ALDER), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildPensjonTP("Ordning 2", TpYtelseType.OVERGANGSTILLEGG), target);

        assertThat(target.getPensjonforvalter().getTp(), hasItems(
                TpOrdning.builder()
                        .ordning("Ordning 1")
                        .ytelser(List.of(TpYtelse.builder()
                                .type(TpYtelseType.ALDER)
                                .build()))
                        .build(),
                TpOrdning.builder()
                        .ordning("Ordning 2")
                        .ytelser(List.of(TpYtelse.builder()
                                .type(TpYtelseType.OVERGANGSTILLEGG)
                                .build()))
                        .build()));
    }

    @Test
    void shouldAccumulatePensjonsavtale() {

        var target = mapperFacade.map(buildPensjonsavtale(AvtaleKategori.FOLKETRYGD, "Minstepensjon"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildPensjonsavtale(AvtaleKategori.PRIVAT_AFP, "Fet pensjon"), target);

        assertThat(target.getPensjonforvalter().getPensjonsavtale(), hasItems(
                Pensjonsavtale.builder()
                        .avtaleKategori(AvtaleKategori.FOLKETRYGD)
                        .produktBetegnelse("Minstepensjon")
                        .build(),
                Pensjonsavtale.builder()
                        .avtaleKategori(AvtaleKategori.PRIVAT_AFP)
                        .produktBetegnelse("Fet pensjon")
                        .build()));
    }

    @Test
    void shouldCopyArenadata() {

        var bestilling = RsDollyUtvidetBestilling.builder()
                .arenaforvalter(Arenadata.builder()
                        .arenaBrukertype(ArenaBrukertype.MED_SERVICEBEHOV)
                        .aktiveringDato(LocalDateTime.of(2020, 1, 1, 0, 0))
                        .kvalifiseringsgruppe(ArenaKvalifiseringsgruppe.BKART)
                        .automatiskInnsendingAvMeldekort(true)
                        .inaktiveringDato(LocalDateTime.of(2026, 1, 1, 0, 0))
                        .build())
                .build();

        var target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getArenaforvalter().getArenaBrukertype(), is(equalTo(ArenaBrukertype.MED_SERVICEBEHOV)));
        assertThat(target.getArenaforvalter().getAktiveringDato(), is(equalTo(LocalDateTime.of(2020, 1, 1, 0, 0))));
        assertThat(target.getArenaforvalter().getKvalifiseringsgruppe(), is(equalTo(ArenaKvalifiseringsgruppe.BKART)));
        assertThat(target.getArenaforvalter().getAutomatiskInnsendingAvMeldekort(), is(equalTo(true)));
        assertThat(target.getArenaforvalter().getInaktiveringDato(), is(equalTo(LocalDateTime.of(2026, 1, 1, 0, 0))));
    }

    @Test
    void shouldAccumulateArenaDagpenger() {

        var target = mapperFacade.map(buildArenaDagpenger("2023-01-01", "2023-06-30"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildArenaDagpenger("2024-01-01", "2024-06-30"), target);

        assertThat(target.getArenaforvalter().getDagpenger(), hasItems(
                RsArenaDagpenger.builder()
                        .fraDato(LocalDate.parse("2023-01-01").atStartOfDay())
                        .tilDato(LocalDate.parse("2023-06-30").atStartOfDay())
                        .build(),
                RsArenaDagpenger.builder()
                        .fraDato(LocalDate.parse("2024-01-01").atStartOfDay())
                        .tilDato(LocalDate.parse("2024-06-30").atStartOfDay())
                        .build()));
    }

    @Test
    void shouldAccumulateArenaAap() {

        var target = mapperFacade.map(buildArenaAap("2023-01-01", "2023-06-30"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildArenaAap("2024-01-01", "2024-06-30"), target);

        assertThat(target.getArenaforvalter().getAap(), hasItems(
                RsArenaAap.builder()
                        .fraDato(LocalDate.parse("2023-01-01").atStartOfDay())
                        .tilDato(LocalDate.parse("2023-06-30").atStartOfDay())
                        .build(),
                RsArenaAap.builder()
                        .fraDato(LocalDate.parse("2024-01-01").atStartOfDay())
                        .tilDato(LocalDate.parse("2024-06-30").atStartOfDay())
                        .build()));
    }

    @Test
    void shouldAccumulateArenaAap115() {

        var target = mapperFacade.map(buildArenaAap115("2023-01-01", "2023-06-30"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildArenaAap115("2024-01-01", "2024-06-30"), target);

        assertThat(target.getArenaforvalter().getAap115(), hasItems(
                RsArenaAap115.builder()
                        .fraDato(LocalDate.parse("2023-01-01").atStartOfDay())
                        .tilDato(LocalDate.parse("2023-06-30").atStartOfDay())
                        .build(),
                RsArenaAap115.builder()
                        .fraDato(LocalDate.parse("2024-01-01").atStartOfDay())
                        .tilDato(LocalDate.parse("2024-06-30").atStartOfDay())
                        .build()));
    }

    @Test
    void shouldCopyNomdata() {

        val bestilling = RsDollyUtvidetBestilling.builder()
                .nomdata(RsNomData.builder()
                        .startDato(LocalDate.parse("2023-01-01").atStartOfDay())
                        .sluttDato(LocalDate.parse("2025-12-31").atStartOfDay())
                        .build())
                .build();

        val target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getNomdata().getStartDato(), is(equalTo(LocalDate.parse("2023-01-01").atStartOfDay())));
        assertThat(target.getNomdata().getSluttDato(), is(equalTo(LocalDate.parse("2025-12-31").atStartOfDay())));
    }

    @Test
    void shouldCopyMedl() {

        val bestilling = RsDollyUtvidetBestilling.builder()
                .medl(RsMedl.builder()
                        .dekning("FULL")
                        .grunnlag("TEST_GRUNNLAG")
                        .lovvalg("NORSK_LOV")
                        .kildedokument("TEST_DOKUMENT")
                        .studieinformasjon(RsMedl.Studieinformasjon.builder()
                                .soeknadInnvilget(true)
                                .build())
                        .build())
                .build();

        val target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getMedl().getDekning(), is(equalTo("FULL")));
        assertThat(target.getMedl().getGrunnlag(), is(equalTo("TEST_GRUNNLAG")));
        assertThat(target.getMedl().getLovvalg(), is(equalTo("NORSK_LOV")));
        assertThat(target.getMedl().getKildedokument(), is(equalTo("TEST_DOKUMENT")));
        assertThat(target.getMedl().getStudieinformasjon().getSoeknadInnvilget(), is(equalTo(true)));
    }

    @Test
    void shouldCopyKrrstub() {

        val bestilling = RsDollyUtvidetBestilling.builder()
                .krrstub(RsDigitalKontaktdata.builder()
                        .epost("tull@toeys.com")
                        .mobil("12345678")
                        .gyldigFra(LocalDate.parse("2023-01-01"))
                        .landkode("+47")
                        .reservert(true)
                        .build())
                .build();

        val target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getKrrstub().getEpost(), is(equalTo("tull@toeys.com")));
        assertThat(target.getKrrstub().getMobil(), is(equalTo("12345678")));
        assertThat(target.getKrrstub().getGyldigFra(), is(equalTo(LocalDate.parse("2023-01-01"))));
        assertThat(target.getKrrstub().getLandkode(), is(equalTo("+47")));
        assertThat(target.getKrrstub().isReservert(), is(equalTo(true)));
    }

    @Test
    void shouldCopyInntektsmelding() {

        val bestilling = RsDollyUtvidetBestilling.builder()
                .inntektsmelding(RsInntektsmelding.builder()
                        .joarkMetadata(RsInntektsmelding.JoarkMetadata.builder()
                                .tema("TEST")
                                .journalpostType("TYPE")
                                .kanal("KANAL")
                                .build())
                        .inntekter(List.of(Inntektsmelding.builder()
                                .arbeidsgiver(RsArbeidsgiver.builder()
                                        .virksomhetsnummer("123456789")
                                        .kontaktinformasjon(RsKontaktinformasjon.builder()
                                                .kontaktinformasjonNavn("Arbeidsgiver AS")
                                                .telefonnummer("99887766")
                                                .build())
                                        .build())
                                .build()))
                        .build())
                .build();

        val target = mapperFacade.map(bestilling, RsDollyUtvidetBestilling.class);

        assertThat(target.getInntektsmelding().getJoarkMetadata().getTema(), is(equalTo("TEST")));
        assertThat(target.getInntektsmelding().getJoarkMetadata().getJournalpostType(), is(equalTo("TYPE")));
        assertThat(target.getInntektsmelding().getJoarkMetadata().getKanal(), is(equalTo("KANAL")));
        assertThat(target.getInntektsmelding().getInntekter(), hasItem(Inntektsmelding.builder()
                .arbeidsgiver(RsArbeidsgiver.builder()
                        .virksomhetsnummer("123456789")
                        .kontaktinformasjon(RsKontaktinformasjon.builder()
                                .kontaktinformasjonNavn("Arbeidsgiver AS")
                                .telefonnummer("99887766")
                                .build())
                        .build())
                .build()));
    }

    @Test
    void shouldAccumulateInntektstub() {

        var target = mapperFacade.map(buildInntektstub(31321.0, InntektType.LOENNSINNTEKT, "Lønn for oktober"), RsDollyUtvidetBestilling.class);
        mapperFacade.map(buildInntektstub(2131322.0, InntektType.NAERINGSINNTEKT, "Inntekt fra næring"), target);

        assertThat(target.getInntektstub().getInntektsinformasjon(), hasItems(
                RsInntektsinformasjon.builder()
                        .inntektsliste(List.of(Inntekt.builder()
                                .beloep(31321.0)
                                .inntektstype(InntektType.LOENNSINNTEKT)
                                .beskrivelse("Lønn for oktober")
                                .build()))
                        .build(),
                RsInntektsinformasjon.builder()
                        .inntektsliste(List.of(Inntekt.builder()
                                .beloep(2131322.0)
                                .inntektstype(InntektType.NAERINGSINNTEKT)
                                .beskrivelse("Inntekt fra næring")
                                .build()))
                        .build()));
    }

    private static RsDollyUtvidetBestilling buildInntektstub(Double beloep, InntektType inntektstype, String beskrivelse) {

        return RsDollyUtvidetBestilling.builder()
                .inntektstub(InntektMultiplierWrapper.builder()
                        .inntektsinformasjon(List.of(RsInntektsinformasjon.builder()
                                .inntektsliste(List.of(Inntekt.builder()
                                        .beloep(beloep)
                                        .inntektstype(inntektstype)
                                        .beskrivelse(beskrivelse)
                                        .build()))
                                .build()))
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildArenaAap115(String fraDato, String tilDato) {

        return RsDollyUtvidetBestilling.builder()
                .arenaforvalter(Arenadata.builder()
                        .aap115(List.of(RsArenaAap115.builder()
                                .fraDato(LocalDate.parse(fraDato).atStartOfDay())
                                .tilDato(LocalDate.parse(tilDato).atStartOfDay())
                                .build()))
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildArenaAap(String fraDato, String tilDato) {

        return RsDollyUtvidetBestilling.builder()
                .arenaforvalter(Arenadata.builder()
                        .aap(List.of(RsArenaAap.builder()
                                .fraDato(LocalDate.parse(fraDato).atStartOfDay())
                                .tilDato(LocalDate.parse(tilDato).atStartOfDay())
                                .build()))
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildArenaDagpenger(String fraDato, String tilDato) {

        return RsDollyUtvidetBestilling.builder()
                .arenaforvalter(Arenadata.builder()
                        .dagpenger(List.of(RsArenaDagpenger.builder()
                                .fraDato(LocalDate.parse(fraDato).atStartOfDay())
                                .tilDato(LocalDate.parse(tilDato).atStartOfDay())
                                .build()))
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildPensjonsavtale(AvtaleKategori kategori, String beskrivelse) {

        return RsDollyUtvidetBestilling.builder()
                .pensjonforvalter(PensjonData.builder()
                        .pensjonsavtale(List.of(Pensjonsavtale.builder()
                                .avtaleKategori(kategori)
                                .produktBetegnelse(beskrivelse)
                                .build()))
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildPensjonTP(String ordning, TpYtelseType tpYtelseType) {

        return RsDollyUtvidetBestilling.builder()
                .pensjonforvalter(PensjonData.builder()
                        .tp(List.of(TpOrdning.builder()
                                .ordning(ordning)
                                .ytelser(List.of(TpYtelse.builder()
                                        .type(tpYtelseType)
                                        .build()))
                                .build()))
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildFullmakt(String fullmaktsgiver, String fullmektig) {

        return RsDollyUtvidetBestilling.builder()
                .fullmakt(List.of(RsFullmakt.builder()
                        .fullmaktsgiver(fullmaktsgiver)
                        .fullmektig(fullmektig)
                        .build()))
                .build();
    }

    private static RsDollyUtvidetBestilling buildHistark1() {

        return RsDollyUtvidetBestilling.builder()
                .histark(RsHistark.builder()
                        .dokumenter(List.of(RsHistark.RsHistarkDokument.builder()
                                .antallSider(12)
                                .dokumentReferanse(2323123L)
                                .tittel("Utenlandsk")
                                .enhetsnavn("Enhet 1")
                                .skanner("Skanner 1")
                                .build()))
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildHistark2() {

        return RsDollyUtvidetBestilling.builder()
                .histark(RsHistark.builder()
                        .dokumenter(List.of(RsHistark.RsHistarkDokument.builder()
                                .antallSider(20)
                                .dokumentReferanse(9876543L)
                                .tittel("Norsk")
                                .enhetsnavn("Enhet 2")
                                .skanner("Skanner 2")
                                .build()))
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildDokarkiv1() {

        return RsDollyUtvidetBestilling.builder()
                .dokarkiv(List.of(RsDokarkiv.builder()
                        .tema("TEST")
                        .sak(RsDokarkiv.Sak.builder()
                                .fagsakId("123456")
                                .build())
                        .kanal("TEST")
                        .dokumenter(List.of(RsDokarkiv.Dokument.builder()
                                .brevkode("TEST1")
                                .tittel("Dokument 1")
                                .dokumentvarianter(List.of(RsDokarkiv.Dokument.DokumentVariant.builder()
                                        .dokumentReferanse(1234L)
                                        .filtype("PDF")
                                        .variantformat("ARKIV")
                                        .dokumentReferanse(7856L)
                                        .build()))
                                .build()))
                        .build()))
                .build();
    }

    private static RsDollyUtvidetBestilling buildDokarkiv2() {

        return RsDollyUtvidetBestilling.builder()
                .dokarkiv(List.of(RsDokarkiv.builder()
                        .tema("TEST2")
                        .sak(RsDokarkiv.Sak.builder()
                                .fagsakId("3456122")
                                .build())
                        .kanal("TEST2")
                        .dokumenter(List.of(RsDokarkiv.Dokument.builder()
                                .brevkode("TEST2")
                                .tittel("Dokument 2")
                                .dokumentvarianter(List.of(RsDokarkiv.Dokument.DokumentVariant.builder()
                                        .dokumentReferanse(6757L)
                                        .filtype("DOC")
                                        .variantformat("ORIGINAL")
                                        .dokumentReferanse(8890L)
                                        .build()))
                                .build()))
                        .build()))
                .build();
    }

    private static RsDollyUtvidetBestilling buildYrkesskader1() {

        return RsDollyUtvidetBestilling.builder()
                .yrkesskader(List.of(YrkesskadeRequest.builder()
                        .ferdigstillSak(FerdigstillSak.AVSLAG)
                        .innmelderrolle(InnmelderRolletype.denSkadelidte)
                        .klassifisering(Klassifisering.BAGATELLMESSIGE_SKADER)
                        .build()))
                .build();
    }

    private static RsDollyUtvidetBestilling buildYrkesskader2() {

        return RsDollyUtvidetBestilling.builder()
                .yrkesskader(List.of(YrkesskadeRequest.builder()
                        .ferdigstillSak(FerdigstillSak.GODKJENT)
                        .innmelderrolle(InnmelderRolletype.vergeOgForesatt)
                        .klassifisering(Klassifisering.MELLOMSKADER)
                        .build()))
                .build();
    }

    private static RsDollyUtvidetBestilling buildInstdata1() {

        return RsDollyUtvidetBestilling.builder()
                .instdata(List.of(RsInstdata.builder()
                        .institusjonstype(InstdataInstitusjonstype.AS)
                        .kategori(InstdataKategori.A)
                        .startdato(LocalDate.of(2025, 1, 1).atStartOfDay())
                        .build()))
                .build();
    }

    private static RsDollyUtvidetBestilling buildInstdata2() {

        return RsDollyUtvidetBestilling.builder()
                .instdata(List.of(RsInstdata.builder()
                        .institusjonstype(InstdataInstitusjonstype.FO)
                        .kategori(InstdataKategori.F)
                        .startdato(LocalDate.of(2023, 1, 1).atStartOfDay())
                        .build()))
                .build();
    }

    private static RsDollyUtvidetBestilling buildSigrunstub1() {

        val bestilling = new RsDollyUtvidetBestilling();
        bestilling.setSigrunstub(List.of(RsLignetInntekt.builder()
                .inntektsaar("2023")
                .grunnlag(List.of(KodeverknavnGrunnlag.builder()
                        .tekniskNavn("GRUNNLAG_1")
                        .verdi("GRUNNLAG_2")
                        .build()))
                .build()));
        bestilling.setSigrunstubPensjonsgivende(List.of(RsPensjonsgivendeForFolketrygden.builder()
                .inntektsaar("2023")
                .pensjonsgivendeInntekt(List.of(Map.of("key", "value")))
                .build()));
        bestilling.setSigrunstubSummertSkattegrunnlag(List.of(RsSummertSkattegrunnlag.builder()
                .inntektsaar("2023")
                .grunnlag(List.of(RsSummertSkattegrunnlag.Grunnlag.builder()
                        .kategori("KATEGORI_1")
                        .beloep(2000)
                        .tekniskNavn("TEKNISK_NAVN_1")
                        .andelOverfoertFraBarn(50)
                        .build()))
                .build()));
        return bestilling;
    }

    private static RsDollyUtvidetBestilling buildSigrunstub2() {

        val bestilling = new RsDollyUtvidetBestilling();
        bestilling.setSigrunstub(List.of(RsLignetInntekt.builder()
                .inntektsaar("2025")
                .grunnlag(List.of(KodeverknavnGrunnlag.builder()
                        .tekniskNavn("GRUNNLAG_3")
                        .verdi("GRUNNLAG_4")
                        .build()))
                .build()));
        bestilling.setSigrunstubPensjonsgivende(List.of(RsPensjonsgivendeForFolketrygden.builder()
                .inntektsaar("2025")
                .pensjonsgivendeInntekt(List.of(Map.of("key", "value")))
                .build()));
        bestilling.setSigrunstubSummertSkattegrunnlag(List.of(RsSummertSkattegrunnlag.builder()
                .inntektsaar("2025")
                .grunnlag(List.of(RsSummertSkattegrunnlag.Grunnlag.builder()
                        .kategori("KATEGORI_1")
                        .beloep(2000)
                        .tekniskNavn("TEKNISK_NAVN_1")
                        .andelOverfoertFraBarn(50)
                        .build()))
                .build()));
        return bestilling;
    }

    private static RsDollyUtvidetBestilling buildAareg1() {

        return RsDollyUtvidetBestilling.builder()
                .aareg(List.of(RsAareg.builder()
                        .arbeidsgiver(RsOrganisasjon.builder()
                                .aktoertype("ORG")
                                .orgnummer("123456789")
                                .build())
                        .arbeidsavtale(RsArbeidsavtale.builder()
                                .ansettelsesform("FAST")
                                .yrke("UTVIKLER")
                                .avloenningstype("MAANEDSLONN")
                                .stillingsprosent(100.0)
                                .build())
                        .build()))
                .build();
    }

    private static RsDollyUtvidetBestilling buildAareg2() {

        return RsDollyUtvidetBestilling.builder()
                .aareg(List.of(RsAareg.builder()
                        .arbeidsgiver(RsAktoerPerson.builder()
                                .aktoertype("PERS")
                                .ident("12345678901")
                                .build())
                        .arbeidsavtale(RsArbeidsavtale.builder()
                                .ansettelsesform("LØST")
                                .yrke("SAUEPASSER")
                                .avloenningstype("BETALING I ULL")
                                .stillingsprosent(50.0)
                                .build())
                        .build()))
                .build();
    }

    private static RsDollyUtvidetBestilling buildSkattekort1() {

        return RsDollyUtvidetBestilling.builder()
                .skattekort(SkattekortRequestDTO.builder()
                        .arbeidsgiverSkatt(List.of(ArbeidsgiverSkatt.builder()
                                .arbeidsgiveridentifikator(IdentifikatorForEnhetEllerPerson.builder()
                                        .organisasjonsnummer("123456789")
                                        .build())
                                .arbeidstaker(List.of(Skattekortmelding.builder()
                                        .arbeidstakeridentifikator("10987654321")
                                        .skattekort(Skattekort.builder()
                                                .forskuddstrekk(List.of(Forskuddstrekk.builder()
                                                        .frikort(Frikort.builder()
                                                                .frikortbeloep(50000)
                                                                .build())
                                                        .build()))
                                                .build())
                                        .inntektsaar(2025)
                                        .tilleggsopplysning(List.of(Tilleggsopplysning.KILDESKATT_PAA_PENSJON))
                                        .build()))
                                .build()))
                        .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildSkattekort2() {

        return RsDollyUtvidetBestilling.builder()
                .skattekort(
                        SkattekortRequestDTO.builder()
                                .arbeidsgiverSkatt(List.of(ArbeidsgiverSkatt.builder()
                                        .arbeidsgiveridentifikator(IdentifikatorForEnhetEllerPerson.builder()
                                                .organisasjonsnummer("987654321")
                                                .build())
                                        .arbeidstaker(List.of(Skattekortmelding.builder()
                                                .arbeidstakeridentifikator("12345678901")
                                                .skattekort(Skattekort.builder()
                                                        .forskuddstrekk(List.of(Forskuddstrekk.builder()
                                                                .trekkprosent(Trekkprosent.builder()
                                                                        .prosentsats(28)
                                                                        .build())
                                                                .build()))
                                                        .build())
                                                .inntektsaar(2024)
                                                .tilleggsopplysning(List.of(Tilleggsopplysning.KILDESKATT_PAA_LOENN))
                                                .build()))
                                        .build()))
                                .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildNavCV1() {

        return RsDollyUtvidetBestilling.builder()
                .arbeidsplassenCV(
                        ArbeidsplassenCVDTO.builder()
                                .harBil(true)
                                .sammendrag("Sammendrag")
                                .jobboensker(new ArbeidsplassenCVDTO.Jobboensker())
                                .sistEndretAvNav(true)
                                .sistEndret(LocalDate.of(2023, 10, 10).atStartOfDay())
                                .andreGodkjenninger(List.of(ArbeidsplassenCVDTO.AnnenGodkjenning.builder()
                                        .certificateName("Nav sertifikat")
                                        .build()))
                                .annenErfaring(List.of(ArbeidsplassenCVDTO.AnnenErfaring.builder()
                                        .description("Gjete sauer")
                                        .build()))
                                .arbeidserfaring(List.of(ArbeidsplassenCVDTO.Arbeidserfaring.builder()
                                        .description("Utvikler hos NAV")
                                        .build()))
                                .fagbrev(List.of(ArbeidsplassenCVDTO.Fagbrev.builder()
                                        .type(ArbeidsplassenCVDTO.VocationalCertification.SVENNEBREV_FAGBREV)
                                        .build()))
                                .foererkort(List.of(ArbeidsplassenCVDTO.Foererkort.builder()
                                        .type("klasse B")
                                        .build()))
                                .kompetanser(List.of(ArbeidsplassenCVDTO.Kompetanse.builder()
                                        .title("Java")
                                        .build()))
                                .kurs(List.of(ArbeidsplassenCVDTO.Kurs.builder()
                                        .title("Sveising")
                                        .build()))
                                .offentligeGodkjenninger(List.of(ArbeidsplassenCVDTO.OffentligeGodkjenning.builder()
                                        .title("Sikkerhetsklarering")
                                        .build()))
                                .spraak(List.of(ArbeidsplassenCVDTO.Spraak.builder()
                                        .language("Fremmedlandsk")
                                        .build()))
                                .utdanning(List.of(ArbeidsplassenCVDTO.Utdanning.builder()
                                        .description("Bachelor i noe")
                                        .build()))
                                .build())
                .build();
    }

    private static RsDollyUtvidetBestilling buildNavCV2() {

        return RsDollyUtvidetBestilling.builder()
                .arbeidsplassenCV(
                        ArbeidsplassenCVDTO.builder()
                                .harBil(true)
                                .sammendrag("Sammendrag")
                                .jobboensker(new ArbeidsplassenCVDTO.Jobboensker())
                                .sistEndretAvNav(true)
                                .sistEndret(LocalDate.of(2025, 10, 10).atStartOfDay())
                                .andreGodkjenninger(new ArrayList<>(List.of(ArbeidsplassenCVDTO.AnnenGodkjenning.builder()
                                        .certificateName("Veritas sertifikat")
                                        .build())))
                                .annenErfaring(List.of(ArbeidsplassenCVDTO.AnnenErfaring.builder()
                                        .description("Gjete gjeiter")
                                        .build()))
                                .arbeidserfaring(List.of(ArbeidsplassenCVDTO.Arbeidserfaring.builder()
                                        .description("Sauepassing")
                                        .build()))
                                .fagbrev(List.of(ArbeidsplassenCVDTO.Fagbrev.builder()
                                        .type(ArbeidsplassenCVDTO.VocationalCertification.AUTORISASJON)
                                        .build()))
                                .foererkort(List.of(ArbeidsplassenCVDTO.Foererkort.builder()
                                        .type("klasse A")
                                        .build()))
                                .kompetanser(List.of(ArbeidsplassenCVDTO.Kompetanse.builder()
                                        .title("Python")
                                        .build()))
                                .kurs(List.of(ArbeidsplassenCVDTO.Kurs.builder()
                                        .title("Hagestell")
                                        .build()))
                                .offentligeGodkjenninger(List.of(ArbeidsplassenCVDTO.OffentligeGodkjenning.builder()
                                        .title("Førerkort")
                                        .build()))
                                .spraak(List.of(ArbeidsplassenCVDTO.Spraak.builder()
                                        .language("Utenlandsk")
                                        .build()))
                                .utdanning(List.of(ArbeidsplassenCVDTO.Utdanning.builder()
                                        .description("Bachelor i noe annet")
                                        .build()))
                                .build())
                .build();
    }
}