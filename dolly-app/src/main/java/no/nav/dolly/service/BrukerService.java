package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsBrukerMedTeamsOgFavoritter;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static no.nav.dolly.util.CurrentNavIdentFetcher.getLoggedInNavIdent;

@Service
public class BrukerService {

    @Autowired
    private BrukerRepository brukerRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TestgruppeService gruppeService;

    @Autowired
    private MapperFacade mapperFacade;

    public void opprettBruker(RsBruker bruker) {
        saveBrukerTilDB(mapperFacade.map(bruker, Bruker.class));
    }

    public Bruker saveBrukerTilDB(Bruker b){
        try {
            return brukerRepository.save(b);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Bruker DB constraint er brutt! Kan ikke lagre bruker. Error: " + e.getMessage());
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getRootCause().getMessage(), e);
        }

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
    public Bruker leggTilFavoritter(Collection<Long> gruppeIDer){
        String navIdent = getLoggedInNavIdent();
        List<Testgruppe> grupper = gruppeService.fetchGrupperByIdsIn(gruppeIDer);

        return leggTilFavoritter(navIdent, grupper);
    }

    @Transactional
    public Bruker leggTilFavoritter(String navident, Collection<Testgruppe> grupper){
        Bruker bruker = fetchBruker(navident);
        bruker.getFavoritter().addAll(grupper);
        return brukerRepository.save(bruker);
    }

    @Transactional
    public Bruker fjernFavoritter(Collection<Long> gruppeIDer){
        String navIdent = getLoggedInNavIdent();
        List<Testgruppe> grupper = gruppeService.fetchGrupperByIdsIn(gruppeIDer);

        return fjernFavoritter(navIdent, grupper);
    }

    @Transactional
    public Bruker leggTilTeam(Bruker b, Team t){
        b.getTeams().add(t);
        return saveBrukerTilDB(b);
    }

    @Transactional
    public Bruker fjernFavoritter(String navident, Collection<Testgruppe> grupper){
        Bruker bruker = fetchBruker(navident);
        bruker.getFavoritter().removeAll(grupper);
        grupper.forEach(g ->  g.getFavorisertAv().remove(bruker));
        Bruker lagretBruker = brukerRepository.save(bruker);
        gruppeService.saveGrupper(grupper);
        return lagretBruker;
    }

    public List<Bruker> fetchBrukere() {
        return brukerRepository.findAll();
    }
}
