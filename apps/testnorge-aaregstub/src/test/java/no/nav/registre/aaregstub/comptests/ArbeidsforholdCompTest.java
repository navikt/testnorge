package no.nav.registre.aaregstub.comptests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.aaregstub.arbeidsforhold.ArbeidsforholdsResponse;
import no.nav.registre.aaregstub.arbeidsforhold.Ident;
import no.nav.registre.aaregstub.arbeidsforhold.contents.AnsettelsesPeriode;
import no.nav.registre.aaregstub.arbeidsforhold.contents.AntallTimerForTimeloennet;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsavtale;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsforhold;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidstaker;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Permisjon;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Utenlandsopphold;
import no.nav.registre.aaregstub.provider.rs.AaregstubController;
import no.nav.registre.aaregstub.repository.ArbeidsforholdRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ArbeidsforholdCompTest {

    @Autowired
    private AaregstubController aaregstubController;

    @Autowired
    private ArbeidsforholdRepository arbeidsforholdRepository;

    private String fnr = "01010101010";
    private List<ArbeidsforholdsResponse> arbeidsforholdsResponse;

    @Before
    public void setUp() {
        AnsettelsesPeriode ansettelsesPeriode = AnsettelsesPeriode.builder().fom("1985-08-01T00:00:00").tom("1990-08-01T00:00:00").build();

        Arbeidstaker arbeidstaker = Arbeidstaker.builder()
                .ident(fnr)
                .identtype("FNR")
                .aktoertype("PERS")
                .build();

        ArrayList<AntallTimerForTimeloennet> antallTimerForTimeloennet = new ArrayList<>();
        antallTimerForTimeloennet.add(AntallTimerForTimeloennet.builder()
                .antallTimer(2)
                .build());

        ArrayList<Permisjon> permisjoner = new ArrayList<>();
        permisjoner.add(Permisjon.builder()
                .permisjonsId("A1B2C3")
                .build());

        ArrayList<Utenlandsopphold> utenlandsopphold = new ArrayList<>();
        utenlandsopphold.add(Utenlandsopphold.builder()
                .land("NOR")
                .build());

        arbeidsforholdsResponse = new ArrayList<>();

        buildArbeidsforholdsResponse(ansettelsesPeriode, arbeidstaker, antallTimerForTimeloennet, permisjoner, utenlandsopphold);
    }

    @Test
    public void shouldLagreNyttArbeidsforhold() {
        saveArbeidsforhold();

        ArrayList<Arbeidsforhold> all = (ArrayList<Arbeidsforhold>) arbeidsforholdRepository.findAll();
        Long id = all.get(0).getId();

        Arbeidsforhold arbeidsforhold = arbeidsforholdRepository.findById(id).get();

        assertThat(arbeidsforhold.getArbeidstaker().getIdent(), equalTo(fnr));
        assertThat(arbeidsforhold.getAnsettelsesPeriode().getFom(), equalTo("1985-08-01T00:00:00"));
    }

    @Test
    public void shouldGetArbeidsforhold() {
        saveArbeidsforhold();

        ArrayList<Arbeidsforhold> all = (ArrayList<Arbeidsforhold>) arbeidsforholdRepository.findAll();
        Long id = all.get(0).getId();

        Arbeidsforhold arbeidsforhold = aaregstubController.hentArbeidsforhold(id);

        assertThat(arbeidsforhold.getId(), equalTo(id));
    }

    @Test
    public void shouldGetFnrMedArbeidsforhold() {
        saveArbeidsforhold();

        ArrayList<Arbeidsforhold> all = (ArrayList<Arbeidsforhold>) arbeidsforholdRepository.findAll();
        Long id = all.get(0).getId();

        Ident ident = aaregstubController.hentIdentMedArbeidsforhold(fnr);
        Arbeidsforhold arbeidsforhold = ident.getArbeidsforhold().get(0);
        Arbeidsavtale arbeidsavtale = arbeidsforhold.getArbeidsavtale();
        AntallTimerForTimeloennet antallTimerForTimeloennet = arbeidsforhold.getAntallTimerForTimeloennet().get(0);
        Permisjon permisjon = arbeidsforhold.getPermisjon().get(0);
        Utenlandsopphold utenlandsopphold = arbeidsforhold.getUtenlandsopphold().get(0);

        assertThat(ident.getFnr(), equalTo(fnr));
        assert arbeidsforholdRepository.findById(id).isPresent();
        assertThat(arbeidsforhold.getAnsettelsesPeriode().getFom(), equalTo(arbeidsforholdRepository.findById(id).get().getAnsettelsesPeriode().getFom()));
        assertThat(arbeidsforhold.getAnsettelsesPeriode().getTom(), equalTo(arbeidsforholdRepository.findById(id).get().getAnsettelsesPeriode().getTom()));
        assertThat(antallTimerForTimeloennet.getAntallTimer(), equalTo(arbeidsforholdRepository.findById(id).get().getAntallTimerForTimeloennet().get(0).getAntallTimer()));
        assertThat(arbeidsforhold.getArbeidsforholdID(), equalTo(arbeidsforholdRepository.findById(id).get().getArbeidsforholdID()));
        assertThat(arbeidsforhold.getArbeidsforholdstype(), equalTo(arbeidsforholdRepository.findById(id).get().getArbeidsforholdstype()));
        assertThat(arbeidsavtale.getArbeidstidsordning(), equalTo(arbeidsforholdRepository.findById(id).get().getArbeidsavtale().getArbeidstidsordning()));
        assertThat(arbeidsavtale.getAvtaltArbeidstimerPerUke(), equalTo(arbeidsforholdRepository.findById(id).get().getArbeidsavtale().getAvtaltArbeidstimerPerUke()));
        assertThat(arbeidsavtale.getEndringsdatoStillingsprosent(), equalTo(arbeidsforholdRepository.findById(id).get().getArbeidsavtale().getEndringsdatoStillingsprosent()));
        assertThat(arbeidsavtale.getStillingsprosent(), equalTo(arbeidsforholdRepository.findById(id).get().getArbeidsavtale().getStillingsprosent()));
        assertThat(arbeidsavtale.getYrke(), equalTo(arbeidsforholdRepository.findById(id).get().getArbeidsavtale().getYrke()));
        assertThat(permisjon.getPermisjonsId(), equalTo(arbeidsforholdRepository.findById(id).get().getPermisjon().get(0).getPermisjonsId()));
        assertThat(utenlandsopphold.getLand(), equalTo(arbeidsforholdRepository.findById(id).get().getUtenlandsopphold().get(0).getLand()));
    }

    @Test
    public void shouldGetAlleArbeidstakere() {
        saveArbeidsforhold();

        List<String> strings = aaregstubController.hentAlleArbeidstakere();

        assertThat(strings.size(), is(1));
        assertThat(strings.get(0), equalTo(fnr));
    }

    @Test
    public void shouldDeleteArbeidsforhold() {
        saveArbeidsforhold();

        ArrayList<Arbeidsforhold> arbeidsforhold = (ArrayList<Arbeidsforhold>) arbeidsforholdRepository.findAll();
        Long id = arbeidsforhold.get(0).getId();

        assertThat(arbeidsforholdRepository.findById(id).get().getIdenten().getFnr(), is(fnr));

        aaregstubController.slettArbeidsforhold(id);

        assertThat(arbeidsforholdRepository.findById(id).isPresent(), is(false));
    }

    private void buildArbeidsforholdsResponse(AnsettelsesPeriode ansettelsesPeriode, Arbeidstaker arbeidstaker,
            ArrayList<AntallTimerForTimeloennet> antallTimerForTimeloennet, ArrayList<Permisjon> permisjoner,
            ArrayList<Utenlandsopphold> utenlandsopphold) {
        arbeidsforholdsResponse.add(ArbeidsforholdsResponse.builder()
                .arbeidsforhold(
                        Arbeidsforhold.builder()
                                .ansettelsesPeriode(ansettelsesPeriode)
                                .antallTimerForTimeloennet(antallTimerForTimeloennet)
                                .arbeidsavtale(
                                        Arbeidsavtale.builder()
                                                .arbeidstidsordning("doegnkontinuerligSkiftOgTurnus355")
                                                .avtaltArbeidstimerPerUke(0.0)
                                                .endringsdatoStillingsprosent("1985-08-01T00:00:00")
                                                .stillingsprosent(0.01)
                                                .yrke("990458162")
                                                .build()
                                )
                                .arbeidsforholdID("oAq5SJgOPDHQnERi")
                                .arbeidsforholdstype("ordinaertArbeidsforhold")
                                .arbeidstaker(arbeidstaker)
                                .permisjon(permisjoner)
                                .utenlandsopphold(utenlandsopphold)
                                .build()
                )
                .build());
    }

    private void saveArbeidsforhold() {
        aaregstubController.lagreArbeidsforhold(arbeidsforholdsResponse);
    }
}
