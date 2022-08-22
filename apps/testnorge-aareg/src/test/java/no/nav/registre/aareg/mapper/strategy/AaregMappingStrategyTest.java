package no.nav.registre.aareg.mapper.strategy;

import static java.time.LocalDateTime.of;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

import no.nav.registre.aareg.config.MappingStrategy;
import no.nav.registre.aareg.domain.RsAntallTimerForTimeloennet;
import no.nav.registre.aareg.domain.RsArbeidsavtale;
import no.nav.registre.aareg.domain.RsArbeidsforhold;
import no.nav.registre.aareg.domain.RsOrganisasjon;
import no.nav.registre.aareg.domain.RsPeriode;
import no.nav.registre.aareg.domain.RsPermisjon;
import no.nav.registre.aareg.domain.RsPersonAareg;
import no.nav.registre.aareg.domain.RsUtenlandsopphold;
import no.nav.registre.aareg.testutils.MapperTestUtils;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidsforhold;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Organisasjon;

class AaregMappingStrategyTest {

    private static RsArbeidsforhold rsArbeidsforhold = buildRsArbeidforhold();
    private CustomConverter calendarConverter = new XmlGregorianCalendarCustomMapping();
    private MappingStrategy aaregMappingStrategy = new AaregMappingStrategy();
    private MapperFacade mapper;

    @BeforeEach
    public void setup() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(calendarConverter, aaregMappingStrategy);
    }

    @Test
    void mapAaregArbeidsforholdHoveddel_OK() {
        var arbeidsforhold = mapper.map(buildRsArbeidforhold(), Arbeidsforhold.class);

        assertThat(arbeidsforhold.getAnsettelsesPeriode().getFom(), is(equalTo(mapper.map(rsArbeidsforhold.getAnsettelsesPeriode().getFom(), XMLGregorianCalendar.class))));
        assertThat(arbeidsforhold.getAnsettelsesPeriode().getTom(), is(equalTo(mapper.map(rsArbeidsforhold.getAnsettelsesPeriode().getTom(), XMLGregorianCalendar.class))));
        assertThat(arbeidsforhold.getArbeidsforholdID(), is(equalTo(rsArbeidsforhold.getArbeidsforholdID())));
        assertThat(arbeidsforhold.getArbeidsforholdIDnav(), is(equalTo(rsArbeidsforhold.getArbeidsforholdIDnav())));
        assertThat(((Organisasjon) arbeidsforhold.getArbeidsgiver()).getOrgnummer(), is(equalTo(((RsOrganisasjon) rsArbeidsforhold.getArbeidsgiver()).getOrgnummer())));
        assertThat(arbeidsforhold.getArbeidstaker().getIdent().getIdent(), is(equalTo(rsArbeidsforhold.getArbeidstaker().getIdent())));
        assertThat(arbeidsforhold.getArbeidstaker().getIdent().getType().getKodeRef(), is(equalTo(rsArbeidsforhold.getArbeidstaker().getIdenttype())));
        assertThat(arbeidsforhold.getArbeidsforholdstype().getKodeRef(), is(equalTo(rsArbeidsforhold.getArbeidsforholdstype())));
    }

    @Test
    void mapAaregArbeidsforholdArbeidsavtale_OK() {
        var arbeidsavtale = mapper.map(buildRsArbeidforhold(), Arbeidsforhold.class).getArbeidsavtale();

        assertThat(arbeidsavtale.getAntallKonverterteTimer(), equalTo(BigDecimal.valueOf(rsArbeidsforhold.getArbeidsavtale().getAntallKonverterteTimer())));
        assertThat(arbeidsavtale.getAvtaltArbeidstimerPerUke(), equalTo(BigDecimal.valueOf(rsArbeidsforhold.getArbeidsavtale().getAvtaltArbeidstimerPerUke())));
        assertThat(arbeidsavtale.getStillingsprosent(), equalTo(BigDecimal.valueOf(rsArbeidsforhold.getArbeidsavtale().getStillingsprosent())));
        assertThat(arbeidsavtale.getEndringsdatoStillingsprosent(),
                is(equalTo(mapper.map(rsArbeidsforhold.getArbeidsavtale().getEndringsdatoStillingsprosent(), XMLGregorianCalendar.class))));
        assertThat(arbeidsavtale.getSisteLoennsendringsdato(),
                is(equalTo(mapper.map(rsArbeidsforhold.getArbeidsavtale().getSisteLoennsendringsdato(), XMLGregorianCalendar.class))));
        assertThat(arbeidsavtale.getArbeidstidsordning().getKodeRef(), is(equalTo(rsArbeidsforhold.getArbeidsavtale().getArbeidstidsordning())));
        assertThat(arbeidsavtale.getAvloenningstype().getKodeRef(), is(equalTo(rsArbeidsforhold.getArbeidsavtale().getAvloenningstype())));
        assertThat(arbeidsavtale.getYrke().getKodeRef(), is(equalTo(rsArbeidsforhold.getArbeidsavtale().getYrke())));
    }

    @Test
    void mapAaregArbeidsforholdAntallTimerForTimeloennede_OK() {
        var antallTimerIPerioden = mapper.map(buildRsArbeidforhold(), Arbeidsforhold.class).getAntallTimerForTimeloennet().get(0);

        assertThat(antallTimerIPerioden.getAntallTimer(), equalTo(BigDecimal.valueOf(rsArbeidsforhold.getAntallTimerForTimeloennet().get(0).getAntallTimer())));
        assertThat(antallTimerIPerioden.getPeriode().getFom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getAntallTimerForTimeloennet().get(0).getPeriode().getFom(), XMLGregorianCalendar.class))));
        assertThat(antallTimerIPerioden.getPeriode().getTom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getAntallTimerForTimeloennet().get(0).getPeriode().getTom(), XMLGregorianCalendar.class))));
    }

    private static RsArbeidsforhold buildRsArbeidforhold() {
        return RsArbeidsforhold.builder()
                .ansettelsesPeriode(RsPeriode.builder()
                        .fom(of(2010, 11, 4, 0, 0))
                        .tom(of(2018, 1, 5, 0, 0))
                        .build())
                .arbeidsforholdID("Ansatt")
                .arbeidsforholdIDnav(101L)
                .arbeidstaker(RsPersonAareg.builder()
                        .ident("5345345345")
                        .identtype("FNR")
                        .build())
                .arbeidsgiver(RsOrganisasjon.builder()
                        .orgnummer("354534534544")
                        .build())
                .arbeidsforholdstype("ansatt")
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .yrke("bussjåfør")
                        .avloenningstype("Per time")
                        .arbeidstidsordning("Regulær")
                        .stillingsprosent(20.0)
                        .antallKonverterteTimer(50)
                        .avtaltArbeidstimerPerUke(17.0)
                        .endringsdatoStillingsprosent(of(2014, 1, 8, 0, 0))
                        .sisteLoennsendringsdato(of(2019, 1, 3, 0, 0))
                        .build())
                .antallTimerForTimeloennet(singletonList(RsAntallTimerForTimeloennet.builder()
                        .antallTimer(345D)
                        .periode(RsPeriode.builder()
                                .fom(of(2013, 10, 8, 0, 0))
                                .tom(of(2015, 2, 7, 0, 0))
                                .build())
                        .build()))
                .permisjon(singletonList(RsPermisjon.builder()
                        .permisjonId("123456")
                        .permisjonsPeriode(RsPeriode.builder()
                                .fom(of(2012, 12, 9, 0, 0))
                                .tom(of(2016, 5, 3, 0, 0))
                                .build())
                        .permisjonsprosent(20.0)
                        .permisjonOgPermittering("velferdspermisjon")
                        .build()))
                .utenlandsopphold(singletonList(RsUtenlandsopphold.builder()
                        .land("Flesland")
                        .periode(RsPeriode.builder()
                                .fom(of(2011, 4, 2, 0, 0))
                                .tom(of(2017, 7, 1, 0, 0))
                                .build())
                        .build()))
                .build();
    }

    @Test
    public void mapAaregArbeidsforholdUtenlandsopphold_OK() {
        var utenlandsopphold = mapper.map(buildRsArbeidforhold(), Arbeidsforhold.class).getUtenlandsopphold().get(0);

        assertThat(utenlandsopphold.getLand().getKodeRef(), is(equalTo(rsArbeidsforhold.getUtenlandsopphold().get(0).getLand())));
        assertThat(utenlandsopphold.getPeriode().getFom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getUtenlandsopphold().get(0).getPeriode().getFom(), XMLGregorianCalendar.class))));
        assertThat(utenlandsopphold.getPeriode().getTom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getUtenlandsopphold().get(0).getPeriode().getTom(), XMLGregorianCalendar.class))));
    }

    @Test
    void mapAaregArbeidsforholdPermisjon_OK() {
        var permisjon = mapper.map(buildRsArbeidforhold(), Arbeidsforhold.class).getPermisjon().get(0);

        assertThat(permisjon.getPermisjonsId(), is(equalTo(rsArbeidsforhold.getPermisjon().get(0).getPermisjonId())));
        assertThat(permisjon.getPermisjonsprosent(), equalTo(BigDecimal.valueOf(rsArbeidsforhold.getPermisjon().get(0).getPermisjonsprosent())));
        assertThat(permisjon.getPermisjonsPeriode().getFom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getPermisjon().get(0).getPermisjonsPeriode().getFom(), XMLGregorianCalendar.class))));
        assertThat(permisjon.getPermisjonsPeriode().getTom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getPermisjon().get(0).getPermisjonsPeriode().getTom(), XMLGregorianCalendar.class))));
        assertThat(permisjon.getPermisjonOgPermittering().getKodeRef(),
                is(equalTo(rsArbeidsforhold.getPermisjon().get(0).getPermisjonOgPermittering())));
    }
}