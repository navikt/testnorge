package no.nav.regression.scenarios.rest.team;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultSet.RsBruker;
import no.nav.dolly.domain.resultSet.RsTeam;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.RsTeamBuilder;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class oppdaterTeamMedNyEierScenarios extends TeamTestCaseBase{

    @Autowired
    MapperFacade mapperFacade;

    @Test
    public void hentAlleTeamBrukerEierEllerErMedlemAv() throws Exception {
        Bruker nyEier = brukerRepository.save(BrukerBuilder.builder().navIdent("nyEier").build().convertToRealBruker());

        Team teamSomSkalEndres = teamRepository.findAll().get(0);

        RsTeam request = RsTeamBuilder.builder()
                .id(teamSomSkalEndres.getId())
                .eierNavIdent(nyEier.getNavIdent())
                .beskrivelse("endretTeam")
                .navn("endretTeamNavn")
                .medlemmer(mapperFacade.mapAsSet(standardTeam.getMedlemmer(),RsBruker.class))
                .datoOpprettet(standardTeam.getDatoOpprettet())
                .build()
                .convertToRealRsTeam();

        String url = endpointUrl + "/" + teamSomSkalEndres.getId();

        MvcResult mvcResult = mvcMock.perform(put(url)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(request)))
                .andExpect(status().isOk())
                .andReturn();

        RsTeam resultat = convertMvcResultToObject(mvcResult, RsTeam.class);

        assertThat(resultat.getEierNavIdent(), is("nyEier"));

        teamSomSkalEndres = teamRepository.findTeamById(teamSomSkalEndres.getId());

        assertThat(teamSomSkalEndres.getEier().getNavIdent(), is("nyEier"));
    }

}
