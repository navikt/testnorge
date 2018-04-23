package no.nav.service;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.api.request.CreateTeamRequest;
import no.nav.exceptions.DollyFunctionalException;
import no.nav.jpa.Team;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DuplicateKeyException;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceOpprettTeamTest extends AbstractTeamServiceTest {
	private CreateTeamRequest createTeamRequest;
	
	@Before
	public void setup() {
		createTeamRequest = new CreateTeamRequest(team.getNavn(), team.getBeskrivelse(), team.getEier()
				.getNavIdent());
		when(brukerRepository.findBrukerByNavIdent(anyString())).thenReturn(eier);
	}
	
	@Test
	public void shouldMapOgOppretteTeam() {
		teamService.opprettTeam(createTeamRequest);
		
		ArgumentCaptor<Team> argument = ArgumentCaptor.forClass(Team.class);
		verify(teamRepository).save(argument.capture());
		assertEquals(team.getNavn(), argument.getValue().getNavn());
		assertEquals(team.getBeskrivelse(), argument.getValue().getBeskrivelse());
		assertEquals(team.getEier(), argument.getValue().getEier());
	}
	
	/**
	 * HVIS team eksisterer fra før SÅ skal funksjonell feilmelding kastes med beskrivende melding.
	 * HVIS NonTransientDataAccessException kastes fra teamRepository.save, SÅ skal funksjonell feilmelding kastes med en beskrivende melding.
	 */
	@Test(expected = DollyFunctionalException.class)
	public void shouldThrowFunctionalExceptionWhenDBConstraintIsViolated() {
		Exception e = new Exception("root");
		when(teamRepository.save(any())).thenThrow(new DuplicateKeyException("feilmelding",e));
		teamService.opprettTeam(createTeamRequest);
	}
}