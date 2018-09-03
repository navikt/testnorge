package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsBrukerMedTeamsOgFavoritter;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.exceptions.BrukerNotFoundException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrukerService {

    @Autowired
    private BrukerRepository brukerRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MapperFacade mapperFacade;

    public void opprettBruker(RsBruker bruker) {
        brukerRepository.save(mapperFacade.map(bruker, Bruker.class));
    }

    public Bruker fetchBruker(String navIdent) {
        Bruker bruker = brukerRepository.findBrukerByNavIdent(navIdent);
        if (bruker == null) {
            throw new NotFoundException("Bruker ikke funnet");
        }
        return bruker;
    }

    public Bruker fetchOrCreateBruker(String navIdent) {
        try{
            return fetchBruker(navIdent);
        } catch (NotFoundException e){
            return brukerRepository.save(new Bruker(navIdent));
        }
    }

    public RsBrukerMedTeamsOgFavoritter getBrukerMedTeamsOgFavoritter(String navIdent) {
        Bruker bruker = fetchBruker(navIdent);
        List<Team> teams = teamService.fetchTeamsByMedlemskapInTeams(navIdent);

        RsBruker rsBruker = mapperFacade.map(bruker, RsBruker.class);
        Set<RsTeam> rsTeams = mapperFacade.mapAsSet(teams, RsTeam.class);

        return RsBrukerMedTeamsOgFavoritter.builder()
                .bruker(rsBruker)
                .teams(rsTeams)
                .build();
    }

    @Transactional
    public Bruker addFavoritter(String navident, Collection<Testgruppe> grupper){
        Bruker bruker = fetchBruker(navident);
        bruker.getFavoritter().addAll(grupper);
        return brukerRepository.save(bruker);
    }

    public List<Bruker> fetchBrukere() {
        return brukerRepository.findAll();
    }
}
