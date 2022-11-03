package no.nav.dolly.bestilling.aareg.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.AaregClient;
import no.nav.dolly.bestilling.aareg.mapper.AaregRequestMappingStrategy;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsAnsettelsesPeriode;
import no.nav.dolly.domain.resultset.aareg.RsAntallTimerIPerioden;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsFartoy;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPeriodeAareg;
import no.nav.dolly.domain.resultset.aareg.RsPermisjon;
import no.nav.dolly.domain.resultset.aareg.RsPermittering;
import no.nav.dolly.domain.resultset.aareg.RsUtenlandsopphold;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.ForenkletOppgjoersordningArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.FrilanserArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.MaritimArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.OrdinaerArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class Aareg2RequestMappingStrategyTest {

    private static final LocalDateTime FOM_DATO = LocalDateTime.of(1990, 1, 1, 0, 0);
    private static final LocalDateTime TOM_DATO = LocalDateTime.of(2010, 1, 1, 0, 0);

    private MapperFacade mapperFacade;

    @BeforeEach
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(), new AaregRequestMappingStrategy());
    }

    @Test
    void mapArbeidsgiverOrganisasjon() {

        var kilde = RsAareg.builder()
                .arbeidsgiver(RsOrganisasjon.builder()
                        .orgnummer("947064649")
                        .build())
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getArbeidsgiver(), is(instanceOf(Organisasjon.class)));
        assertThat(((Organisasjon) destinasjon.getArbeidsgiver()).getOrganisasjonsnummer(), is(equalTo("947064649")));
    }

    @Test
    void mapArbeidsgiverPerson() {

        var kilde = RsAareg.builder()
                .arbeidsgiver(RsAktoerPerson.builder()
                        .ident("11223344556")
                        .identtype("FNR")
                        .build())
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getArbeidsgiver(), is(instanceOf(Person.class)));
        assertThat(((Person) destinasjon.getArbeidsgiver()).getOffentligIdent(), is(equalTo("11223344556")));
    }

    @Test
    void mapArbeidstakerIdent() {

        var kilde = RsAareg.builder()
                .build();

        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty(AaregClient.IDENT, "12345678901");

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class, context);

        assertThat(destinasjon.getArbeidstaker().getOffentligIdent(), is(equalTo("12345678901")));
    }

    @Test
    void mapAnsettelsePeriode() {

        var kilde = RsAareg.builder()
                .ansettelsesPeriode(RsAnsettelsesPeriode.builder()
                        .fom(FOM_DATO)
                        .tom(TOM_DATO)
                        .sluttaarsak("Skoft")
                        .build())
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getAnsettelsesperiode().getPeriode().getFom(), is(equalTo(FOM_DATO.toLocalDate())));
        assertThat(destinasjon.getAnsettelsesperiode().getPeriode().getTom(), is(equalTo(TOM_DATO.toLocalDate())));
        assertThat(destinasjon.getAnsettelsesperiode().getSluttaarsak(), is(equalTo("Skoft")));
    }

    @Test
    void mapAntallTimerForTimeloennet() {

        var kilde = RsAareg.builder()
                .antallTimerForTimeloennet(List.of(RsAntallTimerIPerioden.builder()
                        .periode(RsPeriodeAareg.builder()
                                .fom(FOM_DATO)
                                .tom(TOM_DATO)
                                .build())
                        .antallTimer(new BigDecimal("1457"))
                        .build()))
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getAntallTimerForTimeloennet().get(0).getPeriode().getFom(), is(equalTo(FOM_DATO.toLocalDate())));
        assertThat(destinasjon.getAntallTimerForTimeloennet().get(0).getPeriode().getTom(), is(equalTo(TOM_DATO.toLocalDate())));
        assertThat(destinasjon.getAntallTimerForTimeloennet().get(0).getAntallTimer(), is(equalTo(1457.0)));
    }

    @Test
    void mapPermisjon() {

        var kilde = RsAareg.builder()
                .permisjon(List.of(RsPermisjon.builder()
                        .permisjon("permisjonMedForeldrepenger")
                        .permisjonsPeriode(RsPeriodeAareg.builder()
                                .fom(FOM_DATO)
                                .tom(TOM_DATO)
                                .build())
                        .permisjonsprosent(new BigDecimal("100.0"))
                        .permisjonId("1")
                        .build()))
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getPermisjonPermitteringer().get(0).getType(), is(equalTo("permisjonMedForeldrepenger")));
        assertThat(destinasjon.getPermisjonPermitteringer().get(0).getPeriode().getFom(), is(equalTo(FOM_DATO.toLocalDate())));
        assertThat(destinasjon.getPermisjonPermitteringer().get(0).getPeriode().getTom(), is(equalTo(TOM_DATO.toLocalDate())));
        assertThat(destinasjon.getPermisjonPermitteringer().get(0).getProsent(), is(equalTo(100.0)));
        assertThat(destinasjon.getPermisjonPermitteringer().get(0).getPermisjonPermitteringId(), is(equalTo("1")));
    }

    @Test
    void mapPermittering() {

        var kilde = RsAareg.builder()
                .permittering(List.of(RsPermittering.builder()
                        .permitteringsPeriode(RsPeriodeAareg.builder()
                                .fom(FOM_DATO)
                                .tom(TOM_DATO)
                                .build())
                        .permitteringsprosent(new BigDecimal("75.0"))
                        .build()))
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getPermisjonPermitteringer().get(0).getPeriode().getFom(), is(equalTo(FOM_DATO.toLocalDate())));
        assertThat(destinasjon.getPermisjonPermitteringer().get(0).getPeriode().getTom(), is(equalTo(TOM_DATO.toLocalDate())));
        assertThat(destinasjon.getPermisjonPermitteringer().get(0).getProsent(), is(equalTo(75.0)));
        assertThat(destinasjon.getPermisjonPermitteringer().get(0).getType(), is(equalTo("permittering")));
    }

    @Test
    void mapUtenlandsopphold() {

        var kilde = RsAareg.builder()
                .utenlandsopphold(List.of(RsUtenlandsopphold.builder()
                        .periode(RsPeriodeAareg.builder()
                                .fom(FOM_DATO)
                                .tom(TOM_DATO)
                                .build())
                        .land("BRA")
                        .build()))
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getUtenlandsopphold().get(0).getPeriode().getFom(), is(equalTo(FOM_DATO.toLocalDate())));
        assertThat(destinasjon.getUtenlandsopphold().get(0).getPeriode().getTom(), is(equalTo(TOM_DATO.toLocalDate())));
        assertThat(destinasjon.getUtenlandsopphold().get(0).getLandkode(), is(equalTo("BRA")));
    }

    @Test
    void mapArbeidsavtale_OrdinaerType() {

        var kilde = RsAareg.builder()
                .arbeidsforholdId("1")
                .arbeidsforholdstype("ordinaertArbeidsforhold")
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .arbeidstidsordning("ikkeSkift")
                        .yrke("2521106")
                        .avtaltArbeidstimerPerUke(37.5)
                        .stillingsprosent(100.0)
                        .antallKonverterteTimer(124)
                        .sisteLoennsendringsdato(LocalDateTime.of(2022, 5, 1, 0, 0))
                        .ansettelsesform("fast")
                        .endringsdatoStillingsprosent(LocalDateTime.of(2012, 1, 1, 0, 0))
                        .build())
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getArbeidsforholdId(), is(equalTo("1")));
        assertThat(destinasjon.getType(), is(equalTo("ordinaertArbeidsforhold")));
        assertThat(destinasjon.getArbeidsavtaler().get(0), is(instanceOf(OrdinaerArbeidsavtale.class)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getArbeidstidsordning(), is(equalTo("ikkeSkift")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getAnsettelsesform(), is(equalTo("fast")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getStillingsprosent(), is(equalTo(100.0)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getYrke(), is(equalTo("2521106")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getAntallTimerPrUke(), is(equalTo(37.5)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getSistLoennsendring(), is(equalTo(LocalDate.of(2022, 5, 1))));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getSistStillingsendring(), is(equalTo(LocalDate.of(2012, 1, 1))));
    }

    @Test
    void mapArbeidsavtale_forenkletOppgjoersordningType() {

        var kilde = RsAareg.builder()
                .arbeidsforholdstype("forenkletOppgjoersordning")
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .arbeidstidsordning("ikkeSkift")
                        .yrke("2521106")
                        .avtaltArbeidstimerPerUke(37.5)
                        .stillingsprosent(100.0)
                        .antallKonverterteTimer(124)
                        .sisteLoennsendringsdato(LocalDateTime.of(2022, 5, 1, 0, 0))
                        .ansettelsesform("fast")
                        .endringsdatoStillingsprosent(LocalDateTime.of(2012, 1, 1, 0, 0))
                        .build())
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getType(), is(equalTo("forenkletOppgjoersordning")));
        assertThat(destinasjon.getArbeidsavtaler().get(0), is(instanceOf(ForenkletOppgjoersordningArbeidsavtale.class)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getArbeidstidsordning(), is(equalTo("ikkeSkift")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getAnsettelsesform(), is(equalTo("fast")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getStillingsprosent(), is(equalTo(100.0)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getYrke(), is(equalTo("2521106")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getAntallTimerPrUke(), is(equalTo(37.5)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getSistLoennsendring(), is(equalTo(LocalDate.of(2022, 5, 1))));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getSistStillingsendring(), is(equalTo(LocalDate.of(2012, 1, 1))));
    }

    @Test
    void mapArbeidsavtale_frilansType() {

        var kilde = RsAareg.builder()
                .arbeidsforholdstype("frilanserOppdragstakerHonorarPersonerMm")
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .arbeidstidsordning("ikkeSkift")
                        .yrke("2521106")
                        .avtaltArbeidstimerPerUke(37.5)
                        .stillingsprosent(100.0)
                        .antallKonverterteTimer(124)
                        .sisteLoennsendringsdato(LocalDateTime.of(2022, 5, 1, 0, 0))
                        .ansettelsesform("fast")
                        .endringsdatoStillingsprosent(LocalDateTime.of(2012, 1, 1, 0, 0))
                        .build())
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getType(), is(equalTo("frilanserOppdragstakerHonorarPersonerMm")));
        assertThat(destinasjon.getArbeidsavtaler().get(0), is(instanceOf(FrilanserArbeidsavtale.class)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getArbeidstidsordning(), is(equalTo("ikkeSkift")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getAnsettelsesform(), is(equalTo("fast")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getStillingsprosent(), is(equalTo(100.0)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getYrke(), is(equalTo("2521106")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getAntallTimerPrUke(), is(equalTo(37.5)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getSistLoennsendring(), is(equalTo(LocalDate.of(2022, 5, 1))));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getSistStillingsendring(), is(equalTo(LocalDate.of(2012, 1, 1))));
    }

    @Test
    void mapFartoy() {

        var kilde = RsAareg.builder()
                .arbeidsforholdstype("maritimtArbeidsforhold")
                .fartoy(List.of(RsFartoy.builder()
                        .fartsomraade("utenriks")
                        .skipsregister("nis")
                        .skipstype("turist")
                        .build()))
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getArbeidsavtaler().get(0), is(instanceOf(MaritimArbeidsavtale.class)));
        assertThat(((MaritimArbeidsavtale) destinasjon.getArbeidsavtaler().get(0)).getFartsomraade(), is(equalTo("utenriks")));
        assertThat(((MaritimArbeidsavtale) destinasjon.getArbeidsavtaler().get(0)).getSkipsregister(), is(equalTo("nis")));
        assertThat(((MaritimArbeidsavtale) destinasjon.getArbeidsavtaler().get(0)).getSkipstype(), is(equalTo("turist")));
    }

    @Test
    void mapFartoy2() {

        var kilde = RsAareg.builder()
                .arbeidsforholdstype("ordinaertArbeidsforhold")
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .arbeidstidsordning("ikkeSkift")
                        .yrke("2521106")
                        .avtaltArbeidstimerPerUke(37.5)
                        .stillingsprosent(100.0)
                        .antallKonverterteTimer(124)
                        .sisteLoennsendringsdato(LocalDateTime.of(2022, 5, 1, 0, 0))
                        .ansettelsesform("fast")
                        .endringsdatoStillingsprosent(LocalDateTime.of(2012, 1, 1, 0, 0))
                        .build())
                .fartoy(List.of(RsFartoy.builder()
                        .fartsomraade("utenriks")
                        .skipsregister("nis")
                        .skipstype("turist")
                        .build()))
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getArbeidsavtaler().get(0), is(instanceOf(OrdinaerArbeidsavtale.class)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getArbeidstidsordning(), is(equalTo("ikkeSkift")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getAnsettelsesform(), is(equalTo("fast")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getStillingsprosent(), is(equalTo(100.0)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getYrke(), is(equalTo("2521106")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getAntallTimerPrUke(), is(equalTo(37.5)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getSistLoennsendring(), is(equalTo(LocalDate.of(2022, 5, 1))));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getSistStillingsendring(), is(equalTo(LocalDate.of(2012, 1, 1))));
        assertThat(destinasjon.getArbeidsavtaler().get(1), is(instanceOf(MaritimArbeidsavtale.class)));
        assertThat(destinasjon.getArbeidsavtaler().get(1).getArbeidstidsordning(), is(equalTo("ikkeSkift")));
        assertThat(destinasjon.getArbeidsavtaler().get(1).getAnsettelsesform(), is(equalTo("fast")));
        assertThat(destinasjon.getArbeidsavtaler().get(1).getStillingsprosent(), is(equalTo(100.0)));
        assertThat(destinasjon.getArbeidsavtaler().get(1).getYrke(), is(equalTo("2521106")));
        assertThat(destinasjon.getArbeidsavtaler().get(1).getAntallTimerPrUke(), is(equalTo(37.5)));
        assertThat(destinasjon.getArbeidsavtaler().get(1).getSistLoennsendring(), is(equalTo(LocalDate.of(2022, 5, 1))));
        assertThat(destinasjon.getArbeidsavtaler().get(1).getSistStillingsendring(), is(equalTo(LocalDate.of(2012, 1, 1))));
        assertThat(((MaritimArbeidsavtale) destinasjon.getArbeidsavtaler().get(1)).getFartsomraade(), is(equalTo("utenriks")));
        assertThat(((MaritimArbeidsavtale) destinasjon.getArbeidsavtaler().get(1)).getSkipsregister(), is(equalTo("nis")));
        assertThat(((MaritimArbeidsavtale) destinasjon.getArbeidsavtaler().get(1)).getSkipstype(), is(equalTo("turist")));
    }

    @Test
    void mapFartoy3() {

        var kilde = RsAareg.builder()
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .arbeidstidsordning("ikkeSkift")
                        .yrke("2521106")
                        .avtaltArbeidstimerPerUke(37.5)
                        .stillingsprosent(100.0)
                        .antallKonverterteTimer(124)
                        .sisteLoennsendringsdato(LocalDateTime.of(2022, 5, 1, 0, 0))
                        .ansettelsesform("fast")
                        .endringsdatoStillingsprosent(LocalDateTime.of(2012, 1, 1, 0, 0))
                        .build())
                .fartoy(List.of(RsFartoy.builder()
                        .fartsomraade("utenriks")
                        .skipsregister("nis")
                        .skipstype("turist")
                        .build()))
                .build();

        var destinasjon = mapperFacade.map(kilde, Arbeidsforhold.class);

        assertThat(destinasjon.getArbeidsavtaler().get(0), is(instanceOf(MaritimArbeidsavtale.class)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getArbeidstidsordning(), is(equalTo("ikkeSkift")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getAnsettelsesform(), is(equalTo("fast")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getStillingsprosent(), is(equalTo(100.0)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getYrke(), is(equalTo("2521106")));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getAntallTimerPrUke(), is(equalTo(37.5)));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getSistLoennsendring(), is(equalTo(LocalDate.of(2022, 5, 1))));
        assertThat(destinasjon.getArbeidsavtaler().get(0).getSistStillingsendring(), is(equalTo(LocalDate.of(2012, 1, 1))));
        assertThat(((MaritimArbeidsavtale) destinasjon.getArbeidsavtaler().get(0)).getFartsomraade(), is(equalTo("utenriks")));
        assertThat(((MaritimArbeidsavtale) destinasjon.getArbeidsavtaler().get(0)).getSkipsregister(), is(equalTo("nis")));
        assertThat(((MaritimArbeidsavtale) destinasjon.getArbeidsavtaler().get(0)).getSkipstype(), is(equalTo("turist")));
    }
}