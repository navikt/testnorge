package no.nav.dolly.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;
import static no.nav.dolly.util.CurrentAuthentication.getAuthUser;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrukerService {

    private final BrukerRepository brukerRepository;
    private final TestgruppeRepository testgruppeRepository;
    private final TeamRepository teamRepository;
    private final GetUserInfo getUserInfo;
    private final BrukerServiceConsumer brukerServiceConsumer;

    public Bruker fetchBruker(String brukerId) {

        return brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseThrow(() -> new NotFoundException("Bruker ikke funnet"));
    }

    public Bruker fetchOrCreateBruker(String brukerId) {

        if (isBlank(brukerId)) {
            brukerId = getUserId(getUserInfo);
        }
        try {
            return fetchBruker(brukerId);

        } catch (NotFoundException e) {
            return createBruker();
        }
    }

    public Bruker fetchOrCreateBruker() {

        return fetchOrCreateBruker(null);
    }

    public Long getEffectiveIdForCurrentUser() {
        Bruker bruker = fetchOrCreateBruker();

        if (bruker.getGjeldendeTeam() != null) {
            return bruker.getGjeldendeTeam().getId();
        }

        return bruker.getId();
    }

    @CacheEvict(value = { CACHE_BRUKER }, allEntries = true)
    public Bruker createBruker() {
        return brukerRepository.save(getAuthUser(getUserInfo));
    }

    public Bruker leggTilFavoritt(Long gruppeId) {

        var bruker = fetchBruker(getUserId(getUserInfo));

        var gruppe = fetchTestgruppe(gruppeId);
        gruppe.getFavorisertAv().add(bruker);
        bruker.getFavoritter().add(gruppe);

        return bruker;
    }

    public Bruker fjernFavoritt(Long gruppeId) {

        var bruker = fetchBruker(getUserId(getUserInfo));
        var gruppe = fetchTestgruppe(gruppeId);

        bruker.getFavoritter().remove(gruppe);
        saveGruppe(gruppe);
        gruppe.getFavorisertAv().remove(bruker);

        return bruker;
    }

    @Transactional
    public Bruker setGjeldendeTeam(Long teamId) {
        Bruker bruker = fetchOrCreateBruker();

        if (teamId == null) {
            bruker.setGjeldendeTeam(null);
            return brukerRepository.save(bruker);
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Fant ikke team med ID: " + teamId));

        boolean isTeamMember = bruker.getTeamMedlemskap() != null &&
                bruker.getTeamMedlemskap().stream().anyMatch(t -> t.getId().equals(teamId));

        if (!isTeamMember) {
            throw new IllegalArgumentException("Kan ikke sette aktivt team for bruker som ikke er medlem av teamet");
        }

        bruker.setGjeldendeTeam(team);
        return brukerRepository.save(bruker);
    }

    public List<Bruker> fetchBrukere() {

        var brukeren = fetchOrCreateBruker();
        if (brukeren.getBrukertype() == AZURE) {
            return brukerRepository.findAllByOrderById();

        } else {
            var brukere = brukerServiceConsumer.getKollegaerIOrganisasjon(brukeren.getBrukerId())
                    .map(TilgangDTO::getBrukere)
                    .block();

            return brukerRepository.findAllByBrukerIdInOrderByBrukernavn(brukere);
        }
    }

    public void sletteBrukerFavoritterByGroupId(Long groupId) {
        brukerRepository.deleteBrukerFavoritterByGroupId(groupId);
    }

    public List<Team> fetchTeamsForCurrentBruker() {
        Bruker bruker = fetchOrCreateBruker();
        return bruker.getTeamMedlemskap() != null ?
                new ArrayList<>(bruker.getTeamMedlemskap()) :
                new ArrayList<>();
    }

    private Testgruppe fetchTestgruppe(Long gruppeId) {
        return testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId));
    }

    private void saveGruppe(Testgruppe testgruppe) {

        try {
            testgruppeRepository.save(testgruppe);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Testgruppe DB constraint er brutt! Kan ikke lagre testgruppe. Error: " + e.getMessage(), e);
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getMessage(), e);
        }
    }
}
