package no.nav.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.api.resultSet.RsBruker;
import no.nav.exceptions.BrukerNotFoundException;
import no.nav.jpa.Bruker;
import no.nav.repository.BrukerRepository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrukerService {

	@Autowired
	BrukerRepository brukerRepository;

	@Autowired MapperFacade mapperFacade;
	
	public void opprettBruker(RsBruker bruker) {
		brukerRepository.save(mapperFacade.map(bruker, Bruker.class));
	}
	
	public Bruker getBruker(String navIdent) {
		Bruker bruker = brukerRepository.findBrukerByNavIdent(navIdent);

		if(bruker == null){
			throw new BrukerNotFoundException("Bruker ikke funnet");
		}

		return bruker;
	}

	public List<Bruker> getBrukere(){
		return brukerRepository.findAll();
	}
}
