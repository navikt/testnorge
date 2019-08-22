package no.nav.dolly.mapper.strategy;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDateTime.of;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Before;
import org.junit.Test;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.aareg.RsAntallTimerIPerioden;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPeriode;
import no.nav.dolly.domain.resultset.aareg.RsPermisjon;
import no.nav.dolly.domain.resultset.aareg.RsPersonAareg;
import no.nav.dolly.domain.resultset.aareg.RsUtenlandsopphold;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.AntallTimerIPerioden;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidsavtale;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidsforhold;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Organisasjon;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Permisjon;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Utenlandsopphold;

public class AaregMappingStrategyTest {

    private CustomConverter calendarConverter = new XmlGregorianCalendarCustomMapping();
    private MappingStrategy aaregMappingStrategy = new AaregMappingStrategy();

    private MapperFacade mapper;

    private static RsArbeidsforhold rsArbeidsforhold = buildRsArbeidforhold();

    @Before
    public void setup() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(calendarConverter, aaregMappingStrategy);
    }

    @Test
    public void mapAaregArbeidsforholdHoveddel_OK() {

        Arbeidsforhold arbeidsforhold = mapper.map(buildRsArbeidforhold(), Arbeidsforhold.class);

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
    public void mapAaregArbeidsforholdArbeidsavtale_OK() {

        Arbeidsavtale arbeidsavtale = mapper.map(buildRsArbeidforhold(), Arbeidsforhold.class).getArbeidsavtale();

        assertThat(arbeidsavtale.getAntallKonverterteTimer(), is(equalTo(rsArbeidsforhold.getArbeidsavtale().getAntallKonverterteTimer())));
        assertThat(arbeidsavtale.getAvtaltArbeidstimerPerUke(), is(equalTo(rsArbeidsforhold.getArbeidsavtale().getAvtaltArbeidstimerPerUke())));
        assertThat(arbeidsavtale.getStillingsprosent(), is(equalTo(rsArbeidsforhold.getArbeidsavtale().getStillingsprosent())));
        assertThat(arbeidsavtale.getEndringsdatoStillingsprosent(),
                is(equalTo(mapper.map(rsArbeidsforhold.getArbeidsavtale().getEndringsdatoStillingsprosent(), XMLGregorianCalendar.class))));
        assertThat(arbeidsavtale.getSisteLoennsendringsdato(),
                is(equalTo(mapper.map(rsArbeidsforhold.getArbeidsavtale().getSisteLoennsendringsdato(), XMLGregorianCalendar.class))));
        assertThat(arbeidsavtale.getArbeidstidsordning().getKodeRef(), is(equalTo(rsArbeidsforhold.getArbeidsavtale().getArbeidstidsordning())));
        assertThat(arbeidsavtale.getAvloenningstype().getKodeRef(), is(equalTo(rsArbeidsforhold.getArbeidsavtale().getAvloenningstype())));
        assertThat(arbeidsavtale.getYrke().getKodeRef(), is(equalTo(rsArbeidsforhold.getArbeidsavtale().getYrke())));
    }

    @Test
    public void mapAaregArbeidsforholdAntallTimerForTimeloennede_OK() {

        AntallTimerIPerioden antallTimerIPerioden = mapper.map(buildRsArbeidforhold(), Arbeidsforhold.class).getAntallTimerForTimeloennet().get(0);

        assertThat(antallTimerIPerioden.getAntallTimer(), is(equalTo(rsArbeidsforhold.getAntallTimerForTimeloennet().get(0).getAntallTimer())));
        assertThat(antallTimerIPerioden.getPeriode().getFom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getAntallTimerForTimeloennet().get(0).getPeriode().getFom(), XMLGregorianCalendar.class))));
        assertThat(antallTimerIPerioden.getPeriode().getTom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getAntallTimerForTimeloennet().get(0).getPeriode().getTom(), XMLGregorianCalendar.class))));
    }

    @Test
    public void mapAaregArbeidsforholdPermisjon_OK() {

        Permisjon permisjon = mapper.map(buildRsArbeidforhold(), Arbeidsforhold.class).getPermisjon().get(0);

        assertThat(permisjon.getPermisjonsId(), is(equalTo(rsArbeidsforhold.getPermisjon().get(0).getPermisjonsId())));
        assertThat(permisjon.getPermisjonsprosent(), is(equalTo(rsArbeidsforhold.getPermisjon().get(0).getPermisjonsprosent())));
        assertThat(permisjon.getPermisjonsPeriode().getFom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getPermisjon().get(0).getPermisjonsPeriode().getFom(), XMLGregorianCalendar.class))));
        assertThat(permisjon.getPermisjonsPeriode().getTom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getPermisjon().get(0).getPermisjonsPeriode().getTom(), XMLGregorianCalendar.class))));
        assertThat(permisjon.getPermisjonOgPermittering().getKodeRef(),
                is(equalTo(rsArbeidsforhold.getPermisjon().get(0).getPermisjonOgPermittering())));
    }

    @Test
    public void mapAaregArbeidsforholdUtenlandsopphold_OK() {

        Utenlandsopphold utenlandsopphold = mapper.map(buildRsArbeidforhold(), Arbeidsforhold.class).getUtenlandsopphold().get(0);

        assertThat(utenlandsopphold.getLand().getKodeRef(), is(equalTo(rsArbeidsforhold.getUtenlandsopphold().get(0).getLand())));
        assertThat(utenlandsopphold.getPeriode().getFom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getUtenlandsopphold().get(0).getPeriode().getFom(), XMLGregorianCalendar.class))));
        assertThat(utenlandsopphold.getPeriode().getTom(),
                is(equalTo(mapper.map(rsArbeidsforhold.getUtenlandsopphold().get(0).getPeriode().getTom(), XMLGregorianCalendar.class))));
    }

    private static RsArbeidsforhold buildRsArbeidforhold() {

        return RsArbeidsforhold.builder()
                .ansettelsesPeriode(RsPeriode.builder()
                        .fom(of(2010, 11, 4, 0 , 0))
                        .tom(of(2018, 1, 5, 0 , 0))
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
                        .stillingsprosent(valueOf(20))
                        .antallKonverterteTimer(valueOf(50))
                        .avtaltArbeidstimerPerUke(valueOf(17))
                        .endringsdatoStillingsprosent(of(2014, 1, 8, 0 , 0))
                        .sisteLoennsendringsdato(of(2019, 1, 3, 0 , 0))
                        .build())
                .antallTimerForTimeloennet(singletonList(RsAntallTimerIPerioden.builder()
                        .antallTimer(valueOf(345))
                        .periode(RsPeriode.builder()
                                .fom(of(2013, 10, 8, 0 , 0))
                                .tom(of(2015, 2, 7, 0 , 0))
                                .build())
                        .build()))
                .permisjon(singletonList(RsPermisjon.builder()
                        .permisjonsId("123456")
                        .permisjonsPeriode(RsPeriode.builder()
                                .fom(of(2012, 12, 9, 0 , 0))
                                .tom(of(2016, 5, 3, 0 , 0))
                                .build())
                        .permisjonsprosent(valueOf(20))
                        .permisjonOgPermittering("velferdspermisjon")
                        .build()))
                .utenlandsopphold(singletonList(RsUtenlandsopphold.builder()
                        .land("Flesland")
                        .periode(RsPeriode.builder()
                                .fom(of(2011, 4, 2, 0 , 0))
                                .tom(of(2017, 7, 1, 0 , 0))
                                .build())
                        .build()))
                .build();
    }
}