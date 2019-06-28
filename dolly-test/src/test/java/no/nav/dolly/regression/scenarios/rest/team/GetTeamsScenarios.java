package no.nav.dolly.regression.scenarios.rest.team;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

public class GetTeamsScenarios extends TeamTestCaseBase {

    private Bruker bruker2;

    @Before
    public void setupData() {
        bruker2 = brukerRepository.save(Bruker.builder().navIdent("navident2").build());

        Team team2 = teamRepository.save(Team.builder()
                .navn("team2")
                .datoOpprettet(LocalDate.now())
                .beskrivelse("besk2")
                .eier(bruker2)
                .medlemmer(asList(bruker2, standardBruker))
                .build()
        );

        Team team3 = teamRepository.save(Team.builder()
                .navn("team3")
                .datoOpprettet(LocalDate.now())
                .beskrivelse("besk3")
                .eier(bruker2)
                .medlemmer(singletonList(bruker2))
                .build()
        );
    }

    @Test
    public void hentAlleTeamBrukerEierEllerErMedlemAv() throws Exception {
        String url = endpointUrl + "?navIdent=" + standardBruker.getNavIdent();

        MvcResult mvcResult = mvcMock.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        List<RsTeamUtvidet> resultat = convertMvcResultToList(mvcResult, RsTeamUtvidet.class);

        assertThat(resultat.size(), is(2));

        assertThat(resultat, hasItem(both(
                hasProperty(TEAM_PROP_NAVN, equalTo(STANDARD_TEAM_NAVN))).and(
                hasProperty(TEAM_PROP_EIER_IDENT, equalTo(STANDARD_NAV_IDENT)))
        ));

        assertThat(resultat, hasItem(both(
                hasProperty(TEAM_PROP_NAVN, equalTo("team2"))).and(
                hasProperty(TEAM_PROP_EIER_IDENT, equalTo(bruker2.getNavIdent())))
        ));
    }

    @Test
    public void hentAlleTeamsIBasen() throws Exception {

        MvcResult mvcResult = mvcMock.perform(get(endpointUrl))
                .andExpect(status().isOk())
                .andReturn();

        List<RsTeamUtvidet> resultat = convertMvcResultToList(mvcResult, RsTeamUtvidet.class);

        assertThat(resultat.size(), is(3));

        assertThat(resultat, hasItem(both(
                hasProperty(TEAM_PROP_NAVN, equalTo(STANDARD_TEAM_NAVN))).and(
                hasProperty(TEAM_PROP_EIER_IDENT, equalTo(STANDARD_NAV_IDENT)))
        ));

        assertThat(resultat, hasItem(both(
                hasProperty(TEAM_PROP_NAVN, equalTo("team2"))).and(
                hasProperty(TEAM_PROP_EIER_IDENT, equalTo(bruker2.getNavIdent())))
        ));

        assertThat(resultat, hasItem(both(
                hasProperty(TEAM_PROP_NAVN, equalTo("team3"))).and(
                hasProperty(TEAM_PROP_EIER_IDENT, equalTo(bruker2.getNavIdent())))
        ));
    }
}
