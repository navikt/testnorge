package no.nav.dolly.repository;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import no.nav.dolly.LocalAppStarter;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.testdata.builder.TestidentBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LocalAppStarter.class)
@ActiveProfiles(value = "test")
@Transactional
public class TestidentRepositoryTest {

    @Autowired
    BrukerRepository brukerRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    GruppeRepository gruppeRepository;

    @Autowired
    IdentRepository identRepository;

    @Test
    public void saveTestidentTilGruppe() {
        Testident testident = TestidentBuilder.builder().ident("12345").build().convertToRealTestident();
        Bruker bruker = brukerRepository.save(Bruker.builder().navIdent("ident").build());

        Team team = Team.builder()
                .navn("team")
                .datoOpprettet(LocalDate.now())
                .eier(bruker)
                .beskrivelse("besk")
                .build();

        Testgruppe testgruppe = Testgruppe.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .navn("gruppe")
                .hensikt("hensikt")
                .teamtilhoerighet(team)
                .testidenter(singleton(testident))
                .build();

        team.setGrupper(singletonList(testgruppe));
        testident.setTestgruppe(testgruppe);

        teamRepository.save(team);
        gruppeRepository.save(testgruppe);
        identRepository.save(testident);

        Testident ident = identRepository.findByIdent("12345");

        assertThat(ident.getIdent(), is("12345"));
        assertThat(ident.getTestgruppe().getNavn(), is("gruppe"));
    }
}