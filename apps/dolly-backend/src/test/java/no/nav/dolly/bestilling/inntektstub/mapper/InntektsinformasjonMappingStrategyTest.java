package no.nav.dolly.bestilling.inntektstub.mapper;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.domain.resultset.inntektstub.Inntekt;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekter;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.domain.resultset.inntektstub.Tilleggsinformasjon;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.strategy.OffsetDateTimeCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

class InntektsinformasjonMappingStrategyTest {

    private static final LocalDate AAR_MAANED = LocalDate.of(2016, 1, 1);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String AAR_MAANED_STR = "2016-01";
    private static final String ORG_NR = "123456789";
    private static final Double BELOEP = 350000d;
    private static final Double ANTALL = 404d;
    private static final String BESKRIVELSE = "TULL";
    private static final String AARET_BETALINGEN_GJELDER_FOR = "1994";
    private static final int ANTALL_MAANEDER = 36;
    private static final String IDENT = "01010112345";
    private static final LocalDateTime RAPPORTERINGSDATO = LocalDateTime.of(2016, 2, 1, 12, 0);

    private MapperFacade mapperFacade;
    private MappingContext context;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(
                new CustomConverter[]{new LocalDateCustomMapping(), new OffsetDateTimeCustomMapping()},
                new InntektsinformasjonMappingStrategy());
        context = new MappingContext.Factory().getContext();
        context.setProperty("ident", IDENT);
    }

    @Test
    void mapInntektsinformasjon_HappyPath() {

        InntektsinformasjonWrapper result = mapperFacade.map(prepInntektMultiplierWrapper(), InntektsinformasjonWrapper.class);

        assertThat(result.getInntektsinformasjon().getFirst().getAarMaaned(), is(equalTo(AAR_MAANED_STR)));
        assertThat(result.getInntektsinformasjon().getFirst().getVirksomhet(), is(equalTo(ORG_NR)));
        assertThat(result.getInntektsinformasjon().getFirst().getOpplysningspliktig(), is(equalTo(ORG_NR)));
        assertThat(result.getInntektsinformasjon().getFirst().getInntektsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().getFirst().getInntektsliste().getFirst().getStartOpptjeningsperiode(), is(equalTo(AAR_MAANED)));
        assertThat(result.getInntektsinformasjon().getFirst().getInntektsliste().getFirst().getSluttOpptjeningsperiode(), is(equalTo(AAR_MAANED.plusMonths(1).minusDays(1))));
        assertThat(result.getInntektsinformasjon().getFirst().getInntektsliste().getFirst().getBeskrivelse(), is(equalTo(BESKRIVELSE)));
        assertThat(result.getInntektsinformasjon().getFirst().getInntektsliste().getFirst().getTilleggsinformasjon().getBonusFraForsvaret().getAaretUtbetalingenGjelderFor(),
                is(equalTo(AARET_BETALINGEN_GJELDER_FOR)));
        assertThat(result.getInntektsinformasjon().getFirst().getFradragsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().getFirst().getFradragsliste().getFirst().getBeskrivelse(), is(equalTo(BESKRIVELSE)));
        assertThat(result.getInntektsinformasjon().getFirst().getForskuddstrekksliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().getFirst().getForskuddstrekksliste().getFirst().getBeskrivelse(), is(equalTo(BESKRIVELSE)));
    }

    @Test
    void mapInntektsinformasjonWithMulitiplier_HappyPath() {

        InntektMultiplierWrapper inntektMultiplierWrapper = prepInntektMultiplierWrapper();

        InntektsinformasjonWrapper result = mapperFacade.map(inntektMultiplierWrapper, InntektsinformasjonWrapper.class);

        assertThat(result.getInntektsinformasjon().getFirst().getInntektsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(11).getInntektsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(12).getInntektsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(24).getInntektsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(35).getInntektsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
    }

    @Test
    void mapInntektMultiplier_AarMaanedDecrementsBackwardFromSisteAarMaaned() {

        InntektsinformasjonWrapper result = mapperFacade.map(prepInntektMultiplierWrapper(), InntektsinformasjonWrapper.class);

        assertThat(result.getInntektsinformasjon().get(0).getAarMaaned(), is(equalTo(AAR_MAANED.format(FORMATTER))));
        assertThat(result.getInntektsinformasjon().get(1).getAarMaaned(), is(equalTo(AAR_MAANED.minusMonths(1).format(FORMATTER))));
        assertThat(result.getInntektsinformasjon().get(2).getAarMaaned(), is(equalTo(AAR_MAANED.minusMonths(2).format(FORMATTER))));
    }

    @Test
    void mapInntektMultiplier_NorskIdentIsTakenFromContext() {

        InntektsinformasjonWrapper result = mapperFacade.map(prepInntektMultiplierWrapper(), InntektsinformasjonWrapper.class, context);

        assertThat(result.getInntektsinformasjon().getFirst().getNorskIdent(), is(equalTo(IDENT)));
    }

    @Test
    void mapInntektMultiplier_AntallMaanederNullDefaultsToSingleEntry() {

        var inntektMultiplierWrapper = InntektMultiplierWrapper.builder()
                .inntektsinformasjon(singletonList(RsInntektsinformasjon.builder()
                        .sisteAarMaaned(AAR_MAANED.format(FORMATTER))
                        .antallMaaneder(null)
                        .virksomhet(ORG_NR)
                        .opplysningspliktig(ORG_NR)
                        .inntektsliste(singletonList(Inntekt.builder()
                                .beloep(BELOEP)
                                .beskrivelse(BESKRIVELSE)
                                .build()))
                        .build()))
                .build();

        InntektsinformasjonWrapper result = mapperFacade.map(inntektMultiplierWrapper, InntektsinformasjonWrapper.class);

        assertThat(result.getInntektsinformasjon(), hasSize(1));
    }

    @Test
    void mapInntektMultiplier_NegativeAntallMaanederDefaultsToSingleEntry() {

        var inntektMultiplierWrapper = InntektMultiplierWrapper.builder()
                .inntektsinformasjon(singletonList(RsInntektsinformasjon.builder()
                        .sisteAarMaaned(AAR_MAANED.format(FORMATTER))
                        .antallMaaneder(-5)
                        .virksomhet(ORG_NR)
                        .opplysningspliktig(ORG_NR)
                        .inntektsliste(singletonList(Inntekt.builder()
                                .beloep(BELOEP)
                                .beskrivelse(BESKRIVELSE)
                                .build()))
                        .build()))
                .build();

        InntektsinformasjonWrapper result = mapperFacade.map(inntektMultiplierWrapper, InntektsinformasjonWrapper.class);

        assertThat(result.getInntektsinformasjon(), hasSize(1));
    }

    @Test
    void mapInntektMultiplier_WithHistorikk_ResetsVersjonEachMonth() {

        var historikk = List.of(RsInntektsinformasjon.Historikk.builder()
                .inntektsliste(singletonList(Inntekt.builder().beloep(BELOEP).beskrivelse(BESKRIVELSE).build()))
                .fradragsliste(singletonList(RsInntektsinformasjon.Fradrag.builder().beloep(BELOEP).beskrivelse(BESKRIVELSE).build()))
                .forskuddstrekksliste(singletonList(RsInntektsinformasjon.Forskuddstrekk.builder().beloep(BELOEP).beskrivelse(BESKRIVELSE).build()))
                .build());

        var inntektMultiplierWrapper = InntektMultiplierWrapper.builder()
                .inntektsinformasjon(singletonList(RsInntektsinformasjon.builder()
                        .sisteAarMaaned(AAR_MAANED.format(FORMATTER))
                        .antallMaaneder(2)
                        .virksomhet(ORG_NR)
                        .opplysningspliktig(ORG_NR)
                        .inntektsliste(singletonList(Inntekt.builder()
                                .beloep(BELOEP)
                                .beskrivelse(BESKRIVELSE)
                                .build()))
                        .historikk(historikk)
                        .build()))
                .build();

        InntektsinformasjonWrapper result = mapperFacade.map(inntektMultiplierWrapper, InntektsinformasjonWrapper.class, context);

        // 2 måneder * (1 gjeldende + 1 historikkoppføring) = 4 elementer.
        assertThat(result.getInntektsinformasjon(), hasSize(4));
        assertThat(result.getInntektsinformasjon().get(1).getVersjon(), is(equalTo(1)));
        // Versjonstelleren nullstilles for hver måned i stedet for å fortsette å telle oppover.
        assertThat(result.getInntektsinformasjon().get(3).getVersjon(), is(equalTo(1)));
        assertThat(result.getInntektsinformasjon().get(1).getNorskIdent(), is(equalTo(IDENT)));
        assertThat(result.getInntektsinformasjon().get(1).getOpplysningspliktig(), is(equalTo(ORG_NR)));
        assertThat(result.getInntektsinformasjon().get(1).getVirksomhet(), is(equalTo(ORG_NR)));
    }

    @Test
    void mapRsInntekter_HappyPath() {

        InntektsinformasjonWrapper result = mapperFacade.map(prepRsInntekter(1, null), InntektsinformasjonWrapper.class, context);

        assertThat(result.getInntektsinformasjon(), hasSize(1));
        var inntektsinformasjon = result.getInntektsinformasjon().getFirst();
        assertThat(inntektsinformasjon.getAarMaaned(), is(equalTo(AAR_MAANED_STR)));
        assertThat(inntektsinformasjon.getNorskIdent(), is(equalTo(IDENT)));
        assertThat(inntektsinformasjon.getVirksomhet(), is(equalTo(ORG_NR)));
        assertThat(inntektsinformasjon.getOpplysningspliktig(), is(equalTo(ORG_NR)));
        assertThat(inntektsinformasjon.getInntektsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(inntektsinformasjon.getFradragsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(inntektsinformasjon.getForskuddstrekksliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(inntektsinformasjon.getRapporteringsdato(), is(equalTo(RAPPORTERINGSDATO.atOffset(ZoneOffset.UTC))));
    }

    @Test
    void mapRsInntekter_MultipleMonths_AarMaanedIncrementsForward() {

        InntektsinformasjonWrapper result = mapperFacade.map(prepRsInntekter(3, null), InntektsinformasjonWrapper.class, context);

        assertThat(result.getInntektsinformasjon(), hasSize(3));
        assertThat(result.getInntektsinformasjon().get(0).getAarMaaned(), is(equalTo(AAR_MAANED.format(FORMATTER))));
        assertThat(result.getInntektsinformasjon().get(1).getAarMaaned(), is(equalTo(AAR_MAANED.plusMonths(1).format(FORMATTER))));
        assertThat(result.getInntektsinformasjon().get(2).getAarMaaned(), is(equalTo(AAR_MAANED.plusMonths(2).format(FORMATTER))));
    }

    @Test
    void mapRsInntekter_WithHistorikk_AppendsVersjonedEntriesAfterCurrentEntry() {

        var historikk = List.of(
                RsInntekter.Historikk.builder()
                        .inntektsliste(singletonList(Inntekt.builder().beloep(BELOEP).beskrivelse(BESKRIVELSE).build()))
                        .fradragsliste(singletonList(RsInntekter.Fradrag.builder().beloep(BELOEP).beskrivelse(BESKRIVELSE).build()))
                        .forskuddstrekksliste(singletonList(RsInntekter.Forskuddstrekk.builder().beloep(BELOEP).beskrivelse(BESKRIVELSE).build()))
                        .rapporteringsdato(RAPPORTERINGSDATO)
                        .build(),
                RsInntekter.Historikk.builder()
                        .inntektsliste(singletonList(Inntekt.builder().beloep(BELOEP).beskrivelse(BESKRIVELSE).build()))
                        .fradragsliste(singletonList(RsInntekter.Fradrag.builder().beloep(BELOEP).beskrivelse(BESKRIVELSE).build()))
                        .forskuddstrekksliste(singletonList(RsInntekter.Forskuddstrekk.builder().beloep(BELOEP).beskrivelse(BESKRIVELSE).build()))
                        .build());

        InntektsinformasjonWrapper result = mapperFacade.map(prepRsInntekter(1, historikk), InntektsinformasjonWrapper.class, context);

        // 1 gjeldende oppføring + 2 historikkoppføringer for den ene måneden.
        assertThat(result.getInntektsinformasjon(), hasSize(3));
        assertThat(result.getInntektsinformasjon().get(1).getVersjon(), is(equalTo(1)));
        assertThat(result.getInntektsinformasjon().get(2).getVersjon(), is(equalTo(2)));
        assertThat(result.getInntektsinformasjon().get(1).getAarMaaned(), is(equalTo(AAR_MAANED_STR)));
        assertThat(result.getInntektsinformasjon().get(1).getNorskIdent(), is(equalTo(IDENT)));
        assertThat(result.getInntektsinformasjon().get(1).getOpplysningspliktig(), is(equalTo(ORG_NR)));
        assertThat(result.getInntektsinformasjon().get(1).getVirksomhet(), is(equalTo(ORG_NR)));
        assertThat(result.getInntektsinformasjon().get(1).getRapporteringsdato(), is(equalTo(RAPPORTERINGSDATO.atOffset(ZoneOffset.UTC))));
        assertThat(result.getInntektsinformasjon().get(2).getRapporteringsdato(), is(nullValue()));
    }

    @Test
    void mapRsInntekter_EmptyTilleggsinformasjon_IsSetToNull() {

        var rsInntekter = prepRsInntekter(1, null);
        rsInntekter.getInntektsliste().getFirst().setTilleggsinformasjon(Tilleggsinformasjon.builder().build());

        InntektsinformasjonWrapper result = mapperFacade.map(rsInntekter, InntektsinformasjonWrapper.class, context);

        assertThat(result.getInntektsinformasjon().getFirst().getInntektsliste().getFirst().getTilleggsinformasjon(), is(nullValue()));
    }

    private InntektMultiplierWrapper prepInntektMultiplierWrapper() {

        return InntektMultiplierWrapper.builder()
                .inntektsinformasjon(singletonList(RsInntektsinformasjon.builder()
                        .sisteAarMaaned(AAR_MAANED.format(FORMATTER))
                        .antallMaaneder(ANTALL_MAANEDER)
                        .virksomhet(ORG_NR)
                        .opplysningspliktig(ORG_NR)
                        .inntektsliste(singletonList(Inntekt.builder()
                                .beloep(BELOEP)
                                .startOpptjeningsperiode(AAR_MAANED.atStartOfDay())
                                .sluttOpptjeningsperiode(AAR_MAANED.atStartOfDay().plusMonths(1).minusDays(1))
                                .beskrivelse(BESKRIVELSE)
                                .tilleggsinformasjon(Tilleggsinformasjon.builder()
                                        .bonusFraForsvaret(Tilleggsinformasjon.BonusFraForsvaret.builder()
                                                .aaretUtbetalingenGjelderFor(AARET_BETALINGEN_GJELDER_FOR)
                                                .build())
                                        .build())
                                .antall(ANTALL)
                                .build()))
                        .fradragsliste(singletonList(RsInntektsinformasjon.Fradrag.builder()
                                .beloep(BELOEP)
                                .beskrivelse(BESKRIVELSE)
                                .build()))
                        .forskuddstrekksliste(singletonList(RsInntektsinformasjon.Forskuddstrekk.builder()
                                .beloep(BELOEP)
                                .beskrivelse(BESKRIVELSE)
                                .build()))
                        .build()))
                .build();
    }

    private RsInntekter prepRsInntekter(int antallMaaneder, List<RsInntekter.Historikk> historikk) {

        return RsInntekter.builder()
                .startAarMaaned(AAR_MAANED_STR)
                .antallMaaneder(antallMaaneder)
                .virksomhet(ORG_NR)
                .opplysningspliktig(ORG_NR)
                .rapporteringsdato(RAPPORTERINGSDATO)
                .inntektsliste(singletonList(Inntekt.builder()
                        .beloep(BELOEP)
                        .startOpptjeningsperiode(AAR_MAANED.atStartOfDay())
                        .sluttOpptjeningsperiode(AAR_MAANED.atStartOfDay().plusMonths(1).minusDays(1))
                        .beskrivelse(BESKRIVELSE)
                        .antall(ANTALL)
                        .build()))
                .fradragsliste(singletonList(RsInntekter.Fradrag.builder()
                        .beloep(BELOEP)
                        .beskrivelse(BESKRIVELSE)
                        .build()))
                .forskuddstrekksliste(singletonList(RsInntekter.Forskuddstrekk.builder()
                        .beloep(BELOEP)
                        .beskrivelse(BESKRIVELSE)
                        .build()))
                .historikk(historikk)
                .build();
    }
}