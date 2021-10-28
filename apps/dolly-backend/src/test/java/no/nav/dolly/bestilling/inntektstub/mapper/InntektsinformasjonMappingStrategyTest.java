package no.nav.dolly.bestilling.inntektstub.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.domain.resultset.inntektstub.Inntekt;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.domain.resultset.inntektstub.Tilleggsinformasjon;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InntektsinformasjonMappingStrategyTest {

    private static final LocalDate AAR_MAANED = LocalDate.of(2016, 1, 1);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String AAR_MAANED_STR = "2016-01";
    private static final String ORG_NR = "123456789";
    private static final Double BELOEP = 350000d;
    private static final Double ANTALL = 404d;
    private static final String BESKRIVELSE = "TULL";
    private static final String AVLOENNING_TYPE = "PER VERS";
    private static final String YRKE = "Skadedyrbekjemper";
    private static final String AARET_BETALINGEN_GJELDER_FOR = "1994";
    private static final int ANTALL_MAANEDER = 36;

    private MapperFacade mapperFacade;

    @BeforeEach
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(), new InntektsinformasjonMappingStrategy());
    }

    @Test
    public void mapInntektsinformasjon_HappyPath() {

        InntektsinformasjonWrapper result = mapperFacade.map(prepInntektMultiplierWrapper(), InntektsinformasjonWrapper.class);

        assertThat(result.getInntektsinformasjon().get(0).getAarMaaned(), is(equalTo(AAR_MAANED_STR)));
        assertThat(result.getInntektsinformasjon().get(0).getVirksomhet(), is(equalTo(ORG_NR)));
        assertThat(result.getInntektsinformasjon().get(0).getOpplysningspliktig(), is(equalTo(ORG_NR)));
        assertThat(result.getInntektsinformasjon().get(0).getInntektsliste().get(0).getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(0).getInntektsliste().get(0).getStartOpptjeningsperiode(), is(equalTo(AAR_MAANED)));
        assertThat(result.getInntektsinformasjon().get(0).getInntektsliste().get(0).getSluttOpptjeningsperiode(), is(equalTo(AAR_MAANED.plusMonths(1).minusDays(1))));
        assertThat(result.getInntektsinformasjon().get(0).getInntektsliste().get(0).getBeskrivelse(), is(equalTo(BESKRIVELSE)));
        assertThat(result.getInntektsinformasjon().get(0).getInntektsliste().get(0).getTilleggsinformasjon().getBonusFraForsvaret().getAaretUtbetalingenGjelderFor(),
                is(equalTo(AARET_BETALINGEN_GJELDER_FOR)));
        assertThat(result.getInntektsinformasjon().get(0).getFradragsliste().get(0).getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(0).getFradragsliste().get(0).getBeskrivelse(), is(equalTo(BESKRIVELSE)));
        assertThat(result.getInntektsinformasjon().get(0).getArbeidsforholdsliste().get(0).getAvloenningstype(), is(equalTo(AVLOENNING_TYPE)));
        assertThat(result.getInntektsinformasjon().get(0).getArbeidsforholdsliste().get(0).getArbeidsforholdstype(), is(equalTo(BESKRIVELSE)));
        assertThat(result.getInntektsinformasjon().get(0).getArbeidsforholdsliste().get(0).getYrke(), is(equalTo(YRKE)));
        assertThat(result.getInntektsinformasjon().get(0).getForskuddstrekksliste().get(0).getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(0).getForskuddstrekksliste().get(0).getBeskrivelse(), is(equalTo(BESKRIVELSE)));
    }

    @Test
    public void mapInntektsinformasjonWithMulitiplier_HappyPath() {

        InntektMultiplierWrapper inntektMultiplierWrapper = prepInntektMultiplierWrapper();

        InntektsinformasjonWrapper result = mapperFacade.map(inntektMultiplierWrapper, InntektsinformasjonWrapper.class);

        assertThat(result.getInntektsinformasjon().get(0).getInntektsliste().get(0).getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(11).getInntektsliste().get(0).getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(12).getInntektsliste().get(0).getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(24).getInntektsliste().get(0).getBeloep(), is(equalTo(BELOEP)));
        assertThat(result.getInntektsinformasjon().get(35).getInntektsliste().get(0).getBeloep(), is(equalTo(BELOEP)));
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
                        .arbeidsforholdsliste(singletonList(RsInntektsinformasjon.Arbeidsforhold.builder()
                                .avloenningstype(AVLOENNING_TYPE)
                                .arbeidsforholdstype(BESKRIVELSE)
                                .yrke(YRKE)
                                .build()))
                        .forskuddstrekksliste(singletonList(RsInntektsinformasjon.Forskuddstrekk.builder()
                                .beloep(BELOEP)
                                .beskrivelse(BESKRIVELSE)
                                .build()))
                        .build()))
                .build();
    }
}