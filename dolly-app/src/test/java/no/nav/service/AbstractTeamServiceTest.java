package no.nav.service;

import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.repository.BrukerRepository;
import no.nav.repository.TeamRepository;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractTeamServiceTest {
	@Mock
	TeamRepository teamRepository;
	
	@Mock
	BrukerRepository brukerRepository;
	@InjectMocks
	TeamService teamService;
	
	
	Bruker eier = new Bruker("eierId");
	Team team = Team.builder().eier(eier).navn("teamnavn").datoOpprettet(LocalDateTime.now()).build();
	
}
