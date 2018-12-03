package no.nav.dolly.regression.scenarios.rest.team;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.testdata.builder.RsTeamBuilder;

public class OppdaterTeamScenarios extends TeamTestCaseBase{

    private static final String NAV_IDENT = "NYEIER";

    @Autowired
    MapperFacade mapperFacade;

    @Test
    public void oppdaterBrukerMedAlleInputs() throws Exception {
        Bruker nyEier = brukerRepository.save(Bruker.builder().navIdent(NAV_IDENT).build());

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

        assertThat(resultat.getEierNavIdent(), is(NAV_IDENT));

        teamSomSkalEndres = teamRepository.findById(teamSomSkalEndres.getId()).get();

        assertThat(teamSomSkalEndres.getEier().getNavIdent(), is(NAV_IDENT));
    }

    @Test
    public void oppdaterTeamKunNavnOgBeskrivelseIBody() throws Exception {
        Bruker nyEier = brukerRepository.save(Bruker.builder().navIdent(NAV_IDENT).build());

        Team teamSomSkalErEndret = teamRepository.findAll().get(0);

        RsTeam request = RsTeamBuilder.builder()
                .beskrivelse("endretTeam")
                .navn("endretTeamNavn")
                .build()
                .convertToRealRsTeam();

        String url = endpointUrl + "/" + teamSomSkalErEndret.getId();

        MvcResult mvcResult = mvcMock.perform(put(url)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(request)))
                .andExpect(status().isOk())
                .andReturn();

        RsTeam response = convertMvcResultToObject(mvcResult, RsTeam.class);
        teamSomSkalErEndret = teamRepository.findById(teamSomSkalErEndret.getId()).get();

        assertThat(response.getEierNavIdent(), is(standardNavIdent));
        assertThat(response.getNavn(), is("endretTeamNavn"));
        assertThat(response.getBeskrivelse(), is("endretTeam"));

        assertThat(teamSomSkalErEndret.getEier().getNavIdent(), is(standardNavIdent));
        assertThat(teamSomSkalErEndret.getNavn(), is("endretTeamNavn"));
        assertThat(teamSomSkalErEndret.getBeskrivelse(), is("endretTeam"));
    }
}
