package no.nav.dolly.regression.scenarios.rest.team;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

public class OpprettTeamScenarios extends TeamTestCaseBase {

    @Autowired
    MapperFacade mapperFacade;

    @Test
    public void oppretterTeam() throws Exception {
        RsOpprettTeam request = RsOpprettTeam.builder()
                .beskrivelse("testteam")
                .navn("opprettetTeam")
                .build();

        String url = endpointUrl;

        MvcResult mvcResult = mvcMock.perform(post(url)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(request)))
                .andExpect(status().isCreated())
                .andReturn();

        RsTeamUtvidet resultat = convertMvcResultToObject(mvcResult, RsTeamUtvidet.class);

        List<Team> foundTeams = teamRepository.findTeamsByEierOrderByNavn(standardBruker);
        Team opprettetTeam = foundTeams.get(1);

        assertThat(resultat.getEierNavIdent(), is(STANDARD_NAV_IDENT));
        assertThat(resultat.getBeskrivelse(), is("testteam"));
        assertThat(resultat.getNavn(), is("opprettetTeam"));

        assertThat(opprettetTeam, is(notNullValue()));
        assertThat(opprettetTeam.getEier().getNavIdent(), is(standardBruker.getNavIdent()));
    }
}
