package no.nav.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.api.resultSet.RsTestgruppe;
import no.nav.exceptions.NotFoundException;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.repository.BrukerRepository;
import no.nav.repository.GruppeRepository;
import no.nav.repository.TeamRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestgruppeService {
	
	@Autowired
	GruppeRepository gruppeRepository;
	
	@Autowired
	BrukerRepository brukerRepository;
	
	@Autowired
	TeamRepository teamRepository;

	@Autowired
	TeamService teamService;

	@Autowired
	MapperFacade mapperFacade;
	
	public Testgruppe opprettTestgruppe(Long teamId, RsTestgruppe rsTestgruppe) {
		Team team = teamService.fetchTeamById(teamId);
		Testgruppe gruppe = mapperFacade.map(rsTestgruppe, Testgruppe.class);
		gruppe.setTeamtilhoerighet(team);
		gruppe.setDatoEndret(LocalDateTime.now());

		return gruppeRepository.save(gruppe);
	}

	public Testgruppe fetchTestgruppeById(Long gruppeId){
		Testgruppe testgruppe = gruppeRepository.findById(gruppeId);

		if(testgruppe == null) {
			throw new NotFoundException("Finner ikke gruppe baser på gruppeID: " + gruppeId);
		}

		return testgruppe;
	}

	public List<Testgruppe> fetchAlleTestgrupper(){
		return gruppeRepository.findAll();
	}

	
}
