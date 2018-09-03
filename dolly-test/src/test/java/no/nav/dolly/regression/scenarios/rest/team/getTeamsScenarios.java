package no.nav.dolly.regression.scenarios.rest.team;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Before;
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

public class getTeamsScenarios extends TeamTestCaseBase {

    Bruker bruker2;

    @Before
    public void setupData(){
        bruker2 = brukerRepository.save(BrukerBuilder.builder().navIdent("navident2").build().convertToRealBruker());

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
    }

    @Test
    public void hentAlleTeamBrukerEierEllerErMedlemAv() throws Exception {
        String url = endpointUrl + "?navIdent=" + standardBruker.getNavIdent();

        MvcResult mvcResult = mvcMock.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        List<RsTeam> resultat = convertMvcResultToList(mvcResult, RsTeam.class);

        assertThat(resultat.size(), is(2));

        assertThat(resultat, hasItem(both(
                hasProperty(teamPropNavn, equalTo(standardTeamnavn))).and(
                hasProperty(teamPropEierIdent, equalTo(standardNavIdent)))
        ));

        assertThat(resultat, hasItem(both(
                hasProperty(teamPropNavn, equalTo("team2"))).and(
                hasProperty(teamPropEierIdent, equalTo(bruker2.getNavIdent())))
        ));
    }

    @Test
    public void hentAlleTeamsIBasen() throws Exception {
        String url = endpointUrl;

        MvcResult mvcResult = mvcMock.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        List<RsTeam> resultat = convertMvcResultToList(mvcResult, RsTeam.class);

        assertThat(resultat.size(), is(3));

        assertThat(resultat, hasItem(both(
                hasProperty(teamPropNavn, equalTo(standardTeamnavn))).and(
                hasProperty(teamPropEierIdent, equalTo(standardNavIdent)))
        ));

        assertThat(resultat, hasItem(both(
                hasProperty(teamPropNavn, equalTo("team2"))).and(
                hasProperty(teamPropEierIdent, equalTo(bruker2.getNavIdent())))
        ));

        assertThat(resultat, hasItem(both(
                hasProperty(teamPropNavn, equalTo("team3"))).and(
                hasProperty(teamPropEierIdent, equalTo(bruker2.getNavIdent())))
        ));
    }
}
