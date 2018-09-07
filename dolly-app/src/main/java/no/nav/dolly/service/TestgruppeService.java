package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.repository.TestGruppeRepository;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsBrukerMedTeamsOgFavoritter;
import no.nav.dolly.domain.resultset.RsOpprettTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppeMedErMedlemOgFavoritt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static no.nav.dolly.util.CurrentNavIdentFetcher.getLoggedInNavIdent;
import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

@Service
public class TestgruppeService {

    @Autowired
    private TestGruppeRepository testGruppeRepository;

    @Autowired
    private BrukerService brukerService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MapperFacade mapperFacade;

    public RsTestgruppe opprettTestgruppe(RsOpprettTestgruppe rsTestgruppe) {
        Team team = teamService.fetchTeamById(rsTestgruppe.getTeamId());

        OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        Bruker bruker = brukerService.fetchBruker(auth.getPrincipal());

        Testgruppe gruppe = mapperFacade.map(rsTestgruppe, Testgruppe.class);
        gruppe.setTeamtilhoerighet(team);
        gruppe.setDatoEndret(LocalDate.now());
        gruppe.setSistEndretAv(bruker);
        gruppe.setOpprettetAv(bruker);

        Testgruppe savedGruppe = saveGruppeTilDB(gruppe);
        return mapperFacade.map(savedGruppe, RsTestgruppe.class);
    }

    public Testgruppe fetchTestgruppeById(Long gruppeId) {
        Optional<Testgruppe> testgruppe = testGruppeRepository.findById(gruppeId);

        if (testgruppe.isPresent()) {
            return testgruppe.get();
        }

        throw new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId);
    }

    public List<Testgruppe> fetchGrupperByIdsIn(Collection<Long> grupperIDer){
        List<Testgruppe> grupper = testGruppeRepository.findAllById(grupperIDer);

        if(!isNullOrEmpty(grupper)){
            return grupper;
        }

        throw new NotFoundException("Finner ikke grupper basert på IDer : " +  grupperIDer);
    }

    public RsTestgruppeMedErMedlemOgFavoritt rsTestgruppeToRsTestgruppeMedMedlemOgFavoritt(RsTestgruppe gruppe){
        return new ArrayList<>(getRsTestgruppeMedErMedlem(new HashSet<>(Arrays.asList(gruppe)))).get(0);
    }

    public Set<RsTestgruppe> fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(String navIdent) {
        RsBrukerMedTeamsOgFavoritter brukerinfo = brukerService.getBrukerMedTeamsOgFavoritter(navIdent);
        Set<RsTestgruppe> testgrupper = brukerinfo.getBruker().getFavoritter();
        brukerinfo.getTeams().forEach(team -> testgrupper.addAll(team.getGrupper()));

        return testgrupper;
    }

    public Set<RsTestgruppeMedErMedlemOgFavoritt> getRsTestgruppeMedErMedlem(Set<RsTestgruppe> grupper) {
        OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return getRsTestgruppeMedErMedlem(grupper, auth.getPrincipal());
    }

    public Set<RsTestgruppeMedErMedlemOgFavoritt> getRsTestgruppeMedErMedlem(Set<RsTestgruppe> grupper, String navIdent) {
        Set<RsTestgruppeMedErMedlemOgFavoritt> mfGrupper = mapperFacade.mapAsSet(grupper, RsTestgruppeMedErMedlemOgFavoritt.class);

        Bruker bruker = brukerService.fetchBruker(navIdent);
        Set<String> favoritterIDs = bruker.getFavoritter().stream().map(gruppe -> gruppe.getId().toString()).collect(Collectors.toSet());
        Set<String> teamNames = bruker.getTeams().stream().map(team -> team.getNavn()).collect(Collectors.toSet());

        mfGrupper = mfGrupper.stream()
                .map(gruppe -> {
                    RsTestgruppeMedErMedlemOgFavoritt g = gruppe;
                    g.setFavorittIGruppen(favoritterIDs.contains(gruppe.getId().toString()));
                    return g;
                })
                .map(gruppe -> {
                    RsTestgruppeMedErMedlemOgFavoritt g = gruppe;
                    g.setErMedlemAvTeamSomEierGruppe(teamNames.contains(gruppe.getTeam().getNavn()));
                    return g;
                })
                .collect(Collectors.toSet());

        return mfGrupper;
    }

    public Testgruppe saveGruppeTilDB(Testgruppe testgruppe) {
        try {
            return testGruppeRepository.save(testgruppe);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Testgruppe DB constraint er brutt! Kan ikke lagre testgruppe. Error: " + e.getMessage());
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getRootCause().getMessage(), e);
        }
    }

    public List<Testgruppe> saveGrupper(Collection<Testgruppe> testgrupper) {
        try {
            return testGruppeRepository.saveAll(testgrupper);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Testgruppe DB constraint er brutt! Kan ikke lagre testgruppe. Error: " + e.getMessage());
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getRootCause().getMessage(), e);
        }
    }

    @Transactional
    public void slettGruppeById(Long gruppeId){
        testGruppeRepository.deleteTestgruppeById(gruppeId);
    }

    @Transactional
    public RsTestgruppe oppdaterTestgruppe(Long gruppeId, RsOpprettTestgruppe testgruppe) {
        Testgruppe savedGruppe = fetchTestgruppeById(gruppeId);
        Testgruppe requestGruppe = mapperFacade.map(testgruppe, Testgruppe.class);

        Bruker bruker = brukerService.fetchBruker(getLoggedInNavIdent());

        savedGruppe.setHensikt(requestGruppe.getHensikt());
        savedGruppe.setNavn(requestGruppe.getNavn());
        savedGruppe.setSistEndretAv(bruker);
        savedGruppe.setDatoEndret(LocalDate.now());

        Testgruppe endretGruppe = saveGruppeTilDB(savedGruppe);

        return mapperFacade.map(endretGruppe, RsTestgruppe.class);
    }

    public Set<RsTestgruppeMedErMedlemOgFavoritt> getTestgruppeByNavidentOgTeamId(String navIdent, Long teamId){
        Set<RsTestgruppe> grupper;
        if (!isNullOrEmpty(navIdent)) {
            grupper = fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(navIdent);
        } else {
            grupper = mapperFacade.mapAsSet(fetchAlleTestgrupper(), RsTestgruppe.class);
        }

        if (!isNullOrEmpty(teamId)) {
            grupper = grupper.stream().filter(gruppe -> gruppe.getTeam().getId().toString().equals(teamId.toString())).collect(Collectors.toSet());
        }

        if (!isNullOrEmpty(navIdent)) {
            return getRsTestgruppeMedErMedlem(grupper, navIdent);
        }

        return getRsTestgruppeMedErMedlem(grupper);
    }

    public List<Testgruppe> fetchAlleTestgrupper() {
        return testGruppeRepository.findAll();
    }

}
