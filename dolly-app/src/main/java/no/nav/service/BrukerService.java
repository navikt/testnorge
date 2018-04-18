package no.nav.service;

import no.nav.jpa.Bruker;
import no.nav.repository.BrukerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrukerService {
	@Autowired
	BrukerRepository brukerRepository;
	
	public void opprettBruker(String bruker) {
		brukerRepository.save(new Bruker(bruker));
	}
	
}
