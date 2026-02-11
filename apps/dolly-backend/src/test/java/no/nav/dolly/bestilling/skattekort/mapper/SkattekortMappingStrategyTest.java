package no.nav.dolly.bestilling.skattekort.mapper;

import lombok.val;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.skattekort.domain.Forskuddstrekk;
import no.nav.dolly.bestilling.skattekort.domain.Frikort;
import no.nav.dolly.bestilling.skattekort.domain.Resultatstatus;
import no.nav.dolly.bestilling.skattekort.domain.Skattekort;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO.ProsentkortDTO;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO.TabellkortDTO;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortRequest;
import no.nav.dolly.bestilling.skattekort.domain.Skattekortmelding;
import no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning;
import no.nav.dolly.bestilling.skattekort.domain.Trekkode;
import no.nav.dolly.bestilling.skattekort.domain.Trekkprosent;
import no.nav.dolly.bestilling.skattekort.domain.Trekktabell;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class SkattekortMappingStrategyTest {
    private static final String IDENT = "01010112345";

    private static final Integer INNTEKTSAAR = 2026;
    private static final LocalDate UTSTEDT_DATO = LocalDate.of(2026, 1, 1);

    private MapperFacade mapperFacade;

    private MappingContext context;

    @BeforeEach
    void setUp() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new SkattekortMappingStrategy());
        context = MappingContextUtils.getMappingContext();
    }

    @Test
    void shouldMapComplexFrontendStructureToSokosFormat() {

        val kilde = buildSkattekort();

        context.setProperty("ident", IDENT);

        val target = mapperFacade.map(kilde, SkattekortRequest.class, context);

        assertThat(target.getFnr(), is(equalTo(IDENT)));
        assertThat(target.getSkattekort(), is(notNullValue()));
        assertThat(target.getSkattekort().getInntektsaar(), is(equalTo(INNTEKTSAAR)));
        assertThat(target.getSkattekort().getResultatForSkattekort(), is(equalTo(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK.getValue())));
        assertThat(target.getSkattekort().getUtstedtDato(), is(equalTo(UTSTEDT_DATO.toString())));
        assertThat(target.getSkattekort().getForskuddstrekkList(), is(notNullValue()));
    }

    @Test
    void shouldMapForskuddstrekkWithTrekktabel() {

        val kilde = buildSkattekort();
        context.setProperty("ident", IDENT);

        val result = mapperFacade.map(kilde, SkattekortRequest.class, context);

        assertThat(result.getSkattekort().getForskuddstrekkList(), hasItem(
                SkattekortDTO.ForskuddstrekkDTO.builder()
                        .trekkode(Trekkode.LOENN_FRA_NAV.getValue())
                        .trekktabell(TabellkortDTO.builder()
                                .tabell("8020")
                                .prosentSats(40.0)
                                .antallMndForTrekk(12.0)
                                .build())
                        .build()));
    }

    @Test
    void shouldMapForskuddstrekkWithProsenttrekk() {

        val kilde = buildSkattekort();
        context.setProperty("ident", IDENT);

        val result = mapperFacade.map(kilde, SkattekortRequest.class, context);

        assertThat(result.getSkattekort().getForskuddstrekkList(), hasItem(
                SkattekortDTO.ForskuddstrekkDTO.builder()
                        .trekkode(Trekkode.PENSJON_FRA_NAV.getValue())
                        .prosentkort(ProsentkortDTO.builder()
                                .prosentSats(30.0)
                                .antallMndForTrekk(10.0)
                                .build())
                        .build()));
    }

    @Test
    void shouldMapForskuddstrekkWithFrikort() {

        val kilde = buildSkattekort();
        context.setProperty("ident", IDENT);

        val result = mapperFacade.map(kilde, SkattekortRequest.class, context);

        assertThat(result.getSkattekort().getForskuddstrekkList(), hasItem(
                SkattekortDTO.ForskuddstrekkDTO.builder()
                        .trekkode(Trekkode.UFOERETRYGD_FRA_NAV.getValue())
                        .frikort(SkattekortDTO.FrikortDTO.builder()
                                .frikortBeloep(50000)
                                .build())
                        .build()));
    }

    @Test
    void shouldMapTilleggsopplysninger() {

        val kilde = buildSkattekort();
        context.setProperty("ident", IDENT);

        val result = mapperFacade.map(kilde, SkattekortRequest.class, context);

        assertThat(result.getSkattekort().getTilleggsopplysningList(), hasItems(
                Tilleggsopplysning.OPPHOLD_PAA_SVALBARD.getValue(),
                Tilleggsopplysning.KILDESKATT_PAA_LOENN.getValue(),
                Tilleggsopplysning.KILDESKATT_PAA_PENSJON.getValue(),
                Tilleggsopplysning.OPPHOLD_I_TILTAKSSONE.getValue()));
    }

    @Test
    void shouldMapReleaseMelding() {

        val kilde = SkattekortDTO.builder()
                        .inntektsaar(INNTEKTSAAR)
                                .build();
        context.setProperty("ident", IDENT);

        val result = mapperFacade.map(kilde, SkattekortRequest.class, context);

        assertThat(result.getSkattekort().getResultatForSkattekort(), is(equalTo(Resultatstatus.IKKE_SKATTEKORT.getValue())));
        assertThat(result.getSkattekort().getUtstedtDato(), is(equalTo(LocalDate.now().toString())));
        assertThat(result.getSkattekort().getInntektsaar(), is(equalTo(INNTEKTSAAR)));
        assertThat(result.getSkattekort().getForskuddstrekkList(), is(nullValue()));
        assertThat(result.getSkattekort().getTilleggsopplysningList(), is(nullValue()));
    }

    private static Skattekortmelding buildSkattekort() {

        return Skattekortmelding.builder()
                .inntektsaar(INNTEKTSAAR)
                .resultatPaaForespoersel(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK)
                .skattekort(Skattekort.builder()
                        .utstedtDato(UTSTEDT_DATO)
                        .forskuddstrekk(List.of(
                                Forskuddstrekk.builder()
                                        .trekktabell(Trekktabell.builder()
                                                .trekkode(Trekkode.LOENN_FRA_NAV)
                                                .tabellnummer("8020")
                                                .prosentsats(40)
                                                .antallMaanederForTrekk(12)
                                                .build())
                                        .build(),
                                Forskuddstrekk.builder()
                                        .trekkprosent(Trekkprosent.builder()
                                                .trekkode(Trekkode.PENSJON_FRA_NAV)
                                                .prosentsats(30)
                                                .antallMaanederForTrekk(10)
                                                .build())
                                        .build(),
                                Forskuddstrekk.builder()
                                        .frikort(Frikort.builder()
                                                .trekkode(Trekkode.UFOERETRYGD_FRA_NAV)
                                                .frikortbeloep(50000)
                                                .build())
                                        .build()))
                        .build())
                .tilleggsopplysning(List.of(Tilleggsopplysning.OPPHOLD_PAA_SVALBARD,
                        Tilleggsopplysning.KILDESKATT_PAA_LOENN,
                        Tilleggsopplysning.KILDESKATT_PAA_PENSJON,
                        Tilleggsopplysning.OPPHOLD_I_TILTAKSSONE))
                .build();
    }
}
