package no.nav.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.exceptions.BrukerNotFoundException;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.resultSet.RsBruker;
import no.nav.resultSet.RsBrukerMedTeamsOgFavoritter;
import no.nav.resultSet.RsTeam;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            throw new BrukerNotFoundException("Bruker ikke funnet");
        }
        return bruker;
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

    public List<Bruker> getBrukere() {
        return brukerRepository.findAll();
    }
}
