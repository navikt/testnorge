package no.nav.regression.scenarios.rest.testgruppe;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultSet.RsTestgruppeMedErMedlemOgFavoritt;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class getTestgrupperScenarios extends TestgruppeTestCaseBase {

    @Test
    public void hentAlleTestgrupperTilknyttetBrukerIgjennomFavoritterOgTeammedlemskap() throws Exception {
        Bruker bruker2 = brukerRepository.save(BrukerBuilder.builder().navIdent("navident2").build().convertToRealBruker());

        Team team2 = teamRepository.save(TeamBuilder.builder()
                .navn("team2")
                .datoOpprettet(LocalDate.now())
                .beskrivelse("besk2")
                .eier(bruker2)
                .medlemmer(new HashSet<>(Arrays.asList(bruker2, standardBruker)))
                .build().convertToRealTeam()
        );

        Team team3 = teamRepository.save(TeamBuilder.builder()
                .navn("team3")
                .datoOpprettet(LocalDate.now())
                .beskrivelse("besk3")
                .eier(bruker2)
                .medlemmer(new HashSet<>(Arrays.asList(bruker2)))
                .build().convertToRealTeam()
        );

        Testgruppe testgruppe2 = testGruppeRepository.save(TestgruppeBuilder.builder()
                .navn("gruppe2")
                .hensikt("hensikt2")
                .opprettetAv(standardBruker)
                .sistEndretAv(standardBruker)
                .datoEndret(LocalDate.now())
                .teamtilhoerighet(team2)
                .build().convertToRealTestgruppe()
        );

        Testgruppe testgruppe3 = testGruppeRepository.save(TestgruppeBuilder.builder()
                .navn("gruppe3")
                .hensikt("hensikt3")
                .opprettetAv(bruker2)
                .sistEndretAv(bruker2)
                .datoEndret(LocalDate.now())
                .teamtilhoerighet(team3)
                .build().convertToRealTestgruppe()
        );

        Testgruppe testgruppe4 = testGruppeRepository.save(TestgruppeBuilder.builder()
                .navn("gruppe4")
                .hensikt("hensikt4")
                .opprettetAv(bruker2)
                .sistEndretAv(bruker2)
                .datoEndret(LocalDate.now())
                .teamtilhoerighet(team3)
                .build().convertToRealTestgruppe()
        );

        standardBruker.getFavoritter().add(testgruppe4);
        brukerRepository.save(standardBruker);

        String url = endpointUrl + "?navIdent=" + standardBruker.getNavIdent();
        MvcResult mvcResult = mvcMock.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        Set<RsTestgruppeMedErMedlemOgFavoritt> resultat = convertMvcResultToSet(mvcResult, RsTestgruppeMedErMedlemOgFavoritt.class);


        /* ASSERT */
        assertThat(resultat.size(), is(3));

        assertThat(resultat, hasItem(both(
                hasProperty("navn", equalTo(standardGruppenavn))).and(
                hasProperty("hensikt", equalTo(standardGruppeHensikt))).and(
                hasProperty("opprettetAvNavIdent", equalTo(standardBruker.getNavIdent())))
                ));

        assertThat(resultat, hasItem(both(
                hasProperty("navn", equalTo("gruppe2"))).and(
                hasProperty("hensikt", equalTo("hensikt2")))
        ));

        assertThat(resultat, hasItem(both(
                hasProperty("navn", equalTo("gruppe4"))).and(
                hasProperty("hensikt", equalTo("hensikt4"))).and(
                hasProperty("opprettetAvNavIdent", equalTo(bruker2.getNavIdent())))
        ));

    }
}
