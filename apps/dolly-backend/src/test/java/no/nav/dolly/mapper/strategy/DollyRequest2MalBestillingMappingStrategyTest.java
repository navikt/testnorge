package no.nav.dolly.mapper.strategy;

import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.etterlatte.EtterlatteYtelse;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.domain.resultset.inst.InstdataInstitusjonstype;
import no.nav.dolly.domain.resultset.inst.InstdataKategori;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.sigrunstub.KodeverknavnGrunnlag;
import no.nav.dolly.domain.resultset.sigrunstub.RsLignetInntekt;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.domain.resultset.sigrunstub.RsSummertSkattegrunnlag;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.ArbeidsgiverSkatt;
import no.nav.testnav.libs.dto.skattekortservice.v1.Forskuddstrekk;
import no.nav.testnav.libs.dto.skattekortservice.v1.Frikort;
import no.nav.testnav.libs.dto.skattekortservice.v1.IdentifikatorForEnhetEllerPerson;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import no.nav.testnav.libs.dto.skattekortservice.v1.Tilleggsopplysning;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkprosent;
import no.nav.testnav.libs.dto.yrkesskade.v1.FerdigstillSak;
import no.nav.testnav.libs.dto.yrkesskade.v1.InnmelderRolletype;
import no.nav.testnav.libs.dto.yrkesskade.v1.Klassifisering;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

//    @Test
//    void shouldAccuulateEnvironments() {
//
//        var target = mapperFacade.map(RsDollyUtvidetBestilling.builder()
//                .environments(Set.of("Q1", "Q4"))
//                .build(), RsDollyUtvidetBestilling.class);
//        mapperFacade.map(RsDollyUtvidetBestilling.builder()
//                .environments(Set.of("Q1", "Q2"))
//                .build(), target);
//
//        assertThat(target.getEnvironments(), hasItems("Q1", "Q2", "Q4"));
//    }

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
                                .tilleggsopplysning(List.of(Tilleggsopplysning.KILDESKATTPENSJONIST))
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
                                        .tilleggsopplysning(List.of(Tilleggsopplysning.KILDESKATTPENSJONIST))
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