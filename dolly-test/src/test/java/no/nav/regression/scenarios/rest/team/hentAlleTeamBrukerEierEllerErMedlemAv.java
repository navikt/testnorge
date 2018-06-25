package no.nav.regression.scenarios.rest.team;

import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.resultSet.RsTeam;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class hentAlleTeamBrukerEierEllerErMedlemAv extends TeamTestCaseBase {

    @Test
    public void hentAlleTeamBrukerEierEllerErMedlemAv() throws Exception {
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

        String url = endpointUrl + "/bruker/" + standardBruker.getNavIdent();
        MvcResult mvcResult = mvcMock.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        Set<RsTeam> resultat = convertMvcResultToSet(mvcResult, RsTeam.class);

        assertThat(resultat.size(), is(2));

        assertThat(resultat, hasItem(both(
                hasProperty(teamPropNavn, equalTo(standardTeamnavn))).and(
                hasProperty(teamPropEierIdent, equalTo(standardNavnIdent)))
        ));

        assertThat(resultat, hasItem(both(
                hasProperty(teamPropNavn, equalTo("team2"))).and(
                hasProperty(teamPropEierIdent, equalTo(bruker2.getNavIdent())))
        ));
    }
}
