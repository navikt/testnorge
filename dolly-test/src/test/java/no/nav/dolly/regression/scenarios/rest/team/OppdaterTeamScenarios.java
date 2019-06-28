package no.nav.dolly.regression.scenarios.rest.team;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import no.nav.dolly.testdata.builder.RsTeamUtvidetBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

public class OppdaterTeamScenarios extends TeamTestCaseBase {

    private static final String NAV_IDENT = "NYEIER";

    @Autowired
    MapperFacade mapperFacade;

    @Test
    public void oppdaterBrukerMedAlleInputs() throws Exception {
        Bruker nyEier = brukerRepository.save(Bruker.builder().navIdent(NAV_IDENT).build());

        Team teamSomSkalEndres = teamRepository.findAllByOrderByNavn().get(0);

        RsTeamUtvidet request = RsTeamUtvidetBuilder.builder()
                .id(teamSomSkalEndres.getId())
                .eierNavIdent(nyEier.getNavIdent())
                .beskrivelse("endretTeam")
                .navn("endretTeamNavn")
                .medlemmer(mapperFacade.mapAsList(standardTeam.getMedlemmer(), RsBruker.class))
                .datoOpprettet(standardTeam.getDatoOpprettet())
                .build()
                .convertToRealRsTeam();

        String url = endpointUrl + "/" + teamSomSkalEndres.getId();

        MvcResult mvcResult = mvcMock.perform(put(url)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(request)))
                .andExpect(status().isOk())
                .andReturn();

        RsTeamUtvidet resultat = convertMvcResultToObject(mvcResult, RsTeamUtvidet.class);

        assertThat(resultat.getEierNavIdent(), is(NAV_IDENT));

        teamSomSkalEndres = teamRepository.findById(teamSomSkalEndres.getId()).get();

        assertThat(teamSomSkalEndres.getEier().getNavIdent(), is(NAV_IDENT));
    }

    @Test
    public void oppdaterTeamKunNavnOgBeskrivelseIBody() throws Exception {
        Bruker nyEier = brukerRepository.save(Bruker.builder().navIdent(NAV_IDENT).build());

        Team teamSomSkalErEndret = teamRepository.findAllByOrderByNavn().get(0);

        RsTeamUtvidet request = RsTeamUtvidetBuilder.builder()
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

        RsTeamUtvidet response = convertMvcResultToObject(mvcResult, RsTeamUtvidet.class);
        teamSomSkalErEndret = teamRepository.findById(teamSomSkalErEndret.getId()).get();

        assertThat(response.getEierNavIdent(), is(STANDARD_NAV_IDENT));
        assertThat(response.getNavn(), is("endretTeamNavn"));
        assertThat(response.getBeskrivelse(), is("endretTeam"));

        assertThat(teamSomSkalErEndret.getEier().getNavIdent(), is(STANDARD_NAV_IDENT));
        assertThat(teamSomSkalErEndret.getNavn(), is("endretTeamNavn"));
        assertThat(teamSomSkalErEndret.getBeskrivelse(), is("endretTeam"));
    }
}
