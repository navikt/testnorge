package no.nav.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.repository.TestGruppeRepository;
import no.nav.exceptions.NotFoundException;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.resultSet.RsBrukerMedTeamsOgFavoritter;
import no.nav.resultSet.RsTestgruppe;
import no.nav.resultSet.RsTestident;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestgruppeService {
	
	@Autowired
	private TestGruppeRepository testGruppeRepository;
	
	@Autowired
	private BrukerRepository brukerRepository;
	
	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private BrukerService brukerService;

	@Autowired
	private TeamService teamService;

	@Autowired
	private MapperFacade mapperFacade;
	
	public Testgruppe opprettTestgruppe(Long teamId, RsTestgruppe rsTestgruppe) {
		Team team = teamService.fetchTeamById(teamId);
		Testgruppe gruppe = mapperFacade.map(rsTestgruppe, Testgruppe.class);
		gruppe.setTeamtilhoerighet(team);
		gruppe.setDatoEndret(LocalDate.now());

		return testGruppeRepository.save(gruppe);
	}

	public Testgruppe fetchTestgruppeById(Long gruppeId){
		Optional<Testgruppe> testgruppe = testGruppeRepository.findById(gruppeId);

		if(testgruppe.isPresent()) {
			return testgruppe.get();
		}

		throw new NotFoundException("Finner ikke gruppe baser på gruppeID: " + gruppeId);
	}

	public Set<RsTestgruppe> fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(String navIdent){
		RsBrukerMedTeamsOgFavoritter brukerinfo = brukerService.getBrukerMedTeamsOgFavoritter(navIdent);
		Set<RsTestgruppe> testgrupper = brukerinfo.getBruker().getFavoritter();
		brukerinfo.getTeams().forEach(team -> testgrupper.addAll(team.getGrupper()));

		return testgrupper;
	}

	//TODO
//	public List<RsTestgruppe> fetchTestgrupperByIdAndTeamtilhoerlighet(String navident){
//		Bruker bruker = brukerRepository.findBrukerByNavIdent(navident);
//
//		List<Team> teams = teamRepository.findTeamByEierOrMedlemmer(bruker);
//
//		return null;
	    //List<Testgruppe> grupper = testGruppeRepository.findByEierOrTeams(bruker, )
//	}

	public void saveAllIdenterToTestgruppe(Long gruppeId, Collection<RsTestident> testidenter){
		Testgruppe testgruppe = fetchTestgruppeById(gruppeId);

		List<Testident> identer = mapperFacade.mapAsList(testidenter, Testident.class);
		testgruppe.getTestidenter().addAll(identer);

		testGruppeRepository.save(testgruppe);
	}

	public List<Testgruppe> fetchAlleTestgrupper(){
		return testGruppeRepository.findAll();
	}

	
}
