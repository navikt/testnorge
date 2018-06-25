package no.nav.service;

import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractTeamServiceTest {

	@Mock
	TeamRepository teamRepository;
	
	@Mock
	BrukerRepository brukerRepository;

	@InjectMocks
	TeamService teamService;

	Bruker eier;
	Team team;

	@Before
	public void setupTestdata() {
//		eier = new Bruker("eierId");
//		team = Team.builder().eier(eier).navn("teamnavn").datoOpprettet(LocalDate.now()).build();
	}
}
