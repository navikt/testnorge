package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Dokument;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.DokumentRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.TEAM;
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
    private final DokumentRepository dokumentRepository;
    private final ObjectMapper objectMapper;

    public Bruker fetchBrukerOrTeamBruker(String brukerId) {

        var gjeldendeBrukerId = getEffectiveIdForUser(brukerId);
        if (isNull(gjeldendeBrukerId)) {
            return brukerRepository.findBrukerByBrukerId(brukerId).
                    orElseThrow(() -> new NotFoundException("Bruker ikke funnet med brukerId: " + brukerId));
        }
        return brukerRepository.findBrukerById(gjeldendeBrukerId)
                .orElseThrow(() -> new NotFoundException("Bruker ikke funnet"));
    }

    public Bruker fetchBrukerByBrukerId(String brukerId) {
        return brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseThrow(() -> new NotFoundException("Bruker ikke funnet"));
    }


    public Bruker fetchCurrentBrukerWithoutTeam() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var dokument = "";
        try {
            dokument = objectMapper.writeValueAsString(authentication);
        } catch (JsonProcessingException e) {
            log.error("Feil ved serialisering av authentication objekt", e);
            dokument = "Feil ved serialisering av authentication objekt";
        }

        dokumentRepository.save(Dokument.builder()
                        .bestillingId(22222222L)
                        .sistOppdatert(LocalDateTime.now())
                        .dokumentType(Dokument.DokumentType.TEST_AUTH)
                        .contents(dokument)
                .build());

        var brukerId = getUserId(getUserInfo);

        return brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseGet(() -> createBruker(null));
    }

    public Bruker fetchBrukerById(Long id) {

        return brukerRepository.findBrukerById(id)
                .orElseThrow(() -> new NotFoundException("Bruker ikke funnet"));
    }

    public Bruker fetchBrukerWithoutTeam(String brukerId) {

        return brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseThrow(() -> new NotFoundException("Bruker ikke funnet"));
    }

    public Bruker fetchOrCreateBruker(String brukerId) {

        if (isBlank(brukerId)) {
            brukerId = getUserId(getUserInfo);
        }
        try {
            return fetchBrukerOrTeamBruker(brukerId);

        } catch (NotFoundException e) {
            return createBruker(null);
        }
    }

    public Bruker fetchOrCreateBruker() {

        return fetchOrCreateBruker(null);
    }

    @CacheEvict(value = { CACHE_BRUKER }, allEntries = true)
    public Bruker createBruker(Bruker bruker) {
        if (nonNull(bruker)) {
            return brukerRepository.save(bruker);
        }
        return brukerRepository.save(getAuthUser(getUserInfo));
    }

    public Bruker leggTilFavoritt(Long gruppeId) {

        var bruker = fetchBrukerOrTeamBruker(getUserId(getUserInfo));

        var gruppe = fetchTestgruppe(gruppeId);
        gruppe.getFavorisertAv().add(bruker);
        bruker.getFavoritter().add(gruppe);

        return bruker;
    }

    public Bruker fjernFavoritt(Long gruppeId) {

        var bruker = fetchBrukerOrTeamBruker(getUserId(getUserInfo));
        var gruppe = fetchTestgruppe(gruppeId);

        bruker.getFavoritter().remove(gruppe);
        saveGruppe(gruppe);
        gruppe.getFavorisertAv().remove(bruker);

        return bruker;
    }

    @Transactional
    public Bruker setRepresentererTeam(Long teamId) {
        var bruker = fetchCurrentBrukerWithoutTeam();

        if (isNull(teamId)) {
            bruker.setRepresentererTeam(null);
            return brukerRepository.save(bruker);
        }

        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Fant ikke team med ID: " + teamId));

        var isTeamMember = nonNull(bruker.getTeamMedlemskap()) &&
                bruker.getTeamMedlemskap().stream().anyMatch(t -> t.getId().equals(teamId));

        if (!isTeamMember) {
            throw new IllegalArgumentException("Kan ikke sette aktivt team for bruker som ikke er medlem av teamet");
        }

        bruker.setRepresentererTeam(team);
        return brukerRepository.save(bruker);
    }

    public List<Bruker> fetchBrukere() {

        var brukeren = fetchOrCreateBruker();
        if (brukeren.getBrukertype() == AZURE || brukeren.getBrukertype() == TEAM) {
            return brukerRepository.findAllByOrderByBrukernavn();

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
        var bruker = fetchCurrentBrukerWithoutTeam();
        return nonNull(bruker.getTeamMedlemskap()) ?
                bruker.getTeamMedlemskap().stream()
                        .sorted(Comparator.comparing(Team::getId))
                        .toList() :
                emptyList();
    }

    private Long getEffectiveIdForUser(String brukerId) {

        var bruker = brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseThrow(() -> new NotFoundException("Fant ikke bruker med brukerID: " + brukerId));
        if (nonNull(bruker.getRepresentererTeam())) {
            var brukerTeam = brukerRepository.findBrukerById(bruker.getRepresentererTeam().getBrukerId())
                    .orElseThrow(() -> new NotFoundException("Fant ikke bruker for team med brukerID: " + bruker.getRepresentererTeam().getBrukerId()));

            return brukerTeam.getId();
        }

        return bruker.getId();
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
