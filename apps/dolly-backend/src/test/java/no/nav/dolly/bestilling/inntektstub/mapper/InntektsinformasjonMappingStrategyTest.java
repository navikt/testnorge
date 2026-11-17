package no.nav.dolly.bestilling.inntektstub.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekt;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekter;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.domain.resultset.inntektstub.RsTilleggsinformasjon;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class InntektsinformasjonMappingStrategyTest {

    private static final LocalDate AAR_MAANED = LocalDate.of(2016, 1, 1);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String AAR_MAANED_STR = "2016-01";
    private static final String ORG_NR = "123456789";
    private static final String IDENT = "12345678901";
    private static final Double BELOEP = 350000d;
    private static final Double ANTALL = 404d;
    private static final String BESKRIVELSE = "TULL";
    private static final String AARET_BETALINGEN_GJELDER_FOR = "1994";

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(), new InntektsinformasjonMappingStrategy());
    }

    @Test
    void shouldMapRsInntektsinformasjon() {

        var context = mappingContext(YearMonth.from(AAR_MAANED), IDENT);
        var result = mapperFacade.map(prepInntektsinformasjon(), Inntektsinformasjon.class, context);

        assertThat(result.getNorskIdent(), is(equalTo(IDENT)));
        assertThat(result.getAarMaaned(), is(equalTo(AAR_MAANED_STR)));
        assertThat(result.getVirksomhet(), is(equalTo(ORG_NR)));
        assertThat(result.getOpplysningspliktig(), is(equalTo(ORG_NR)));
        assertThat(result.getInntektsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsliste().getFirst().getStartOpptjeningsperiode(), is(equalTo(AAR_MAANED)));
        assertThat(result.getInntektsliste().getFirst().getSluttOpptjeningsperiode(), is(equalTo(AAR_MAANED.plusMonths(1).minusDays(1))));
        assertThat(result.getInntektsliste().getFirst().getBeskrivelse(), is(equalTo(BESKRIVELSE)));
        assertThat(result.getInntektsliste().getFirst().getTilleggsinformasjon().getBonusFraForsvaret().getAaretUtbetalingenGjelderFor(),
                is(equalTo(AARET_BETALINGEN_GJELDER_FOR)));
        assertThat(result.getFradragsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getFradragsliste().getFirst().getBeskrivelse(), is(equalTo(BESKRIVELSE)));
        assertThat(result.getForskuddstrekksliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getForskuddstrekksliste().getFirst().getBeskrivelse(), is(equalTo(BESKRIVELSE)));
    }

    @Test
    void shouldMapRsInntekterAndPreserveExplicitEarningPeriodDates() {

        var context = mappingContext(YearMonth.of(2016, 2), IDENT);
        var result = mapperFacade.map(prepRsInntekter(), Inntektsinformasjon.class, context);

        assertThat(result.getNorskIdent(), is(equalTo(IDENT)));
        assertThat(result.getAarMaaned(), is(equalTo("2016-02")));
        assertThat(result.getVirksomhet(), is(equalTo(ORG_NR)));
        assertThat(result.getOpplysningspliktig(), is(equalTo(ORG_NR)));
        assertThat(result.getInntektsliste().getFirst().getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsliste().getFirst().getStartOpptjeningsperiode(), is(equalTo(AAR_MAANED)));
        assertThat(result.getInntektsliste().getFirst().getSluttOpptjeningsperiode(), is(equalTo(LocalDate.of(2016, 1, 31))));
        assertThat(result.getInntektsliste().getFirst().getBeskrivelse(), is(equalTo(BESKRIVELSE)));
        assertThat(result.getInntektsliste().getFirst().getTilleggsinformasjon().getBonusFraForsvaret().getAaretUtbetalingenGjelderFor(),
                is(equalTo(AARET_BETALINGEN_GJELDER_FOR)));
    }

    @ParameterizedTest
    @CsvSource({
            "2016-02, , , 2016-02-01, 2016-02-29",
            "2017-02, , , 2017-02-01, 2017-02-28",
            "2016-02, , 2016-03-31T12:30:00, 2016-02-01, 2016-03-31",
            "2016-02, 2016-01-31T12:30:00, , 2016-01-31, 2016-02-29"
    })
    void shouldDefaultOnlyMissingEarningPeriodDates(YearMonth period, LocalDateTime start, LocalDateTime end,
                                                   LocalDate expectedStart, LocalDate expectedEnd) {

        var result = mapperFacade.map(prepInntekt(start, end),
                no.nav.dolly.bestilling.inntektstub.domain.Inntekt.class, mappingContext(period, IDENT));

        assertThat(result.getStartOpptjeningsperiode(), is(equalTo(expectedStart)));
        assertThat(result.getSluttOpptjeningsperiode(), is(equalTo(expectedEnd)));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldConvertAdditionalInformationPeriodTimestampsToDates(boolean legacyFormat) {
        var additionalInformation = RsTilleggsinformasjon.builder()
                .etterbetalingsperiode(RsTilleggsinformasjon.Periode.builder()
                        .startdato(LocalDateTime.of(2015, 12, 31, 23, 45))
                        .sluttdato(LocalDateTime.of(2016, 2, 29, 12, 30))
                        .build())
                .pensjon(RsTilleggsinformasjon.AldersUfoereEtterlatteAvtalefestetOgKrigspensjon.builder()
                        .tidsrom(RsTilleggsinformasjon.Periode.builder()
                                .startdato(LocalDateTime.of(2014, 3, 15, 8, 15))
                                .sluttdato(LocalDateTime.of(2015, 11, 30, 18, 45))
                                .build())
                        .build())
                .build();

        var result = mapAdditionalInformation(additionalInformation, legacyFormat)
                .getInntektsliste().getFirst().getTilleggsinformasjon();

        assertThat(result.getEtterbetalingsperiode().getStartdato(), is(equalTo(LocalDate.of(2015, 12, 31))));
        assertThat(result.getEtterbetalingsperiode().getSluttdato(), is(equalTo(LocalDate.of(2016, 2, 29))));
        assertThat(result.getPensjon().getTidsrom().getStartdato(), is(equalTo(LocalDate.of(2014, 3, 15))));
        assertThat(result.getPensjon().getTidsrom().getSluttdato(), is(equalTo(LocalDate.of(2015, 11, 30))));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldPreserveMissingDatesInAdditionalInformationPeriods(boolean legacyFormat) {
        var additionalInformation = RsTilleggsinformasjon.builder()
                .etterbetalingsperiode(RsTilleggsinformasjon.Periode.builder()
                        .sluttdato(LocalDateTime.of(2016, 2, 29, 12, 30))
                        .build())
                .pensjon(RsTilleggsinformasjon.AldersUfoereEtterlatteAvtalefestetOgKrigspensjon.builder()
                        .tidsrom(RsTilleggsinformasjon.Periode.builder()
                                .startdato(LocalDateTime.of(2015, 12, 31, 23, 45))
                                .build())
                        .build())
                .build();

        var result = mapAdditionalInformation(additionalInformation, legacyFormat)
                .getInntektsliste().getFirst().getTilleggsinformasjon();

        assertThat(result.getEtterbetalingsperiode().getStartdato(), is(nullValue()));
        assertThat(result.getEtterbetalingsperiode().getSluttdato(), is(equalTo(LocalDate.of(2016, 2, 29))));
        assertThat(result.getPensjon().getTidsrom().getStartdato(), is(equalTo(LocalDate.of(2015, 12, 31))));
        assertThat(result.getPensjon().getTidsrom().getSluttdato(), is(nullValue()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldPreserveMissingAdditionalInformation(boolean legacyFormat) {
        var result = mapAdditionalInformation(null, legacyFormat);

        assertThat(result.getInntektsliste().getFirst().getTilleggsinformasjon(), is(nullValue()));
    }

    private Inntektsinformasjon mapAdditionalInformation(RsTilleggsinformasjon additionalInformation, boolean legacyFormat) {
        var context = mappingContext(YearMonth.of(2016, 2), IDENT);
        if (legacyFormat) {
            var source = prepInntektsinformasjon();
            source.getInntektsliste().getFirst().setTilleggsinformasjon(additionalInformation);
            return mapperFacade.map(source, Inntektsinformasjon.class, context);
        }
        var source = prepRsInntekter();
        source.getInntektsliste().getFirst().setTilleggsinformasjon(additionalInformation);
        return mapperFacade.map(source, Inntektsinformasjon.class, context);
    }

    private static MappingContext mappingContext(YearMonth period, String ident) {

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("periode", period);
        context.setProperty("ident", ident);
        return context;
    }

    private RsInntektsinformasjon prepInntektsinformasjon() {

        return RsInntektsinformasjon.builder()
                .sisteAarMaaned(AAR_MAANED.format(FORMATTER))
                .virksomhet(ORG_NR)
                .opplysningspliktig(ORG_NR)
                .inntektsliste(singletonList(prepInntekt(AAR_MAANED.atStartOfDay(),
                        AAR_MAANED.atStartOfDay().plusMonths(1).minusDays(1))))
                .fradragsliste(singletonList(RsInntektsinformasjon.Fradrag.builder()
                        .beloep(BELOEP)
                        .beskrivelse(BESKRIVELSE)
                        .build()))
                .forskuddstrekksliste(singletonList(RsInntektsinformasjon.Forskuddstrekk.builder()
                        .beloep(BELOEP)
                        .beskrivelse(BESKRIVELSE)
                        .build()))
                .build();
    }

    private RsInntekter prepRsInntekter() {

        return RsInntekter.builder()
                .perioder(singletonList(YearMonth.of(2016, 2)))
                .virksomhet(ORG_NR)
                .opplysningspliktig(ORG_NR)
                .inntektsliste(singletonList(prepInntekt(AAR_MAANED.atStartOfDay(),
                        AAR_MAANED.atStartOfDay().plusMonths(1).minusDays(1))))
                .build();
    }

    private static RsInntekt prepInntekt(LocalDateTime start, LocalDateTime slutt) {

        return RsInntekt.builder()
                .beloep(BELOEP)
                .startOpptjeningsperiode(start)
                .sluttOpptjeningsperiode(slutt)
                .beskrivelse(BESKRIVELSE)
                .tilleggsinformasjon(RsTilleggsinformasjon.builder()
                        .bonusFraForsvaret(RsTilleggsinformasjon.BonusFraForsvaret.builder()
                                .aaretUtbetalingenGjelderFor(AARET_BETALINGEN_GJELDER_FOR)
                                .build())
                        .build())
                .antall(ANTALL)
                .build();
    }
}
