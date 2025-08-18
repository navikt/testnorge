package no.nav.dolly.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Bruker.Brukertype;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.TEAM;
import static no.nav.dolly.util.CurrentAuthentication.getAuthUser;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrukerService {

    private final BrukerFavoritterRepository brukerFavoritterRepository;
    private final BrukerRepository brukerRepository;
    private final BrukerServiceConsumer brukerServiceConsumer;
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final TeamRepository teamRepository;
    private final GetUserInfo getUserInfo;
    private final TestgruppeRepository testgruppeRepository;

    public Mono<Bruker> fetchBruker(String brukerId) {
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
        var brukerId = getUserId(getUserInfo);

        return brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseGet(() -> createBruker(null));
    }

    public Bruker fetchBrukerById(Long id) {

        return brukerRepository.findBrukerById(id)
                .orElseThrow(() -> new NotFoundException("Bruker ikke funnet"));
    }

    public Bruker fetchBrukerWithoutTeam(String brukerId) {

        return brukerRepository.findByBrukerId(brukerId)
                .switchIfEmpty(Mono.error(new NotFoundException("Bruker id: %s ikke funnet".formatted(brukerId))));
    }

    public Mono<Bruker> fetchOrCreateBruker(String brukerId) {

        if (isBlank(brukerId)) {
            return getAuthenticatedUserId.call()
                    .flatMap(this::fetchBruker)
                    .onErrorResume(NotFoundException.class, error -> createBruker());
        } else {
            return fetchBrukerOrTeamBruker(brukerId)
                    .onErrorResume(NotFoundException.class, error -> createBruker(null));
        }
    }

    public Mono<Bruker> fetchOrCreateBruker() {

        return fetchOrCreateBruker(null);
    }

    @CacheEvict(value = {CACHE_BRUKER}, allEntries = true)
    public Mono<Bruker> createBruker() {
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

        return getAuthUser(getUserInfo)
                .flatMap(brukerRepository::save);
    }

    public Mono<Bruker> leggTilFavoritt(Long gruppeId) {

        return fetchTestgruppe(gruppeId)
                .flatMap(gruppe -> fetchOrCreateBruker()
                        .flatMap(bruker -> brukerFavoritterRepository.save(BrukerFavoritter.builder()
                                        .brukerId(bruker.getId())
                                        .gruppeId(gruppe.getId())
                                        .build())
                                .thenReturn(bruker)));
    }
        var bruker = fetchBrukerOrTeamBruker(getUserId(getUserInfo));
        var gruppe = fetchTestgruppe(gruppeId);

    public Mono<Bruker> fjernFavoritt(Long gruppeId) {

        return fetchTestgruppe(gruppeId)
                .flatMap(gruppe ->
                        fetchOrCreateBruker()
                        .flatMap(bruker -> brukerFavoritterRepository.deleteByBrukerIdAndGruppeId(
                                        bruker.getId(),
                                        gruppe.getId())
                                .thenReturn(bruker)));
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

    public Flux<Bruker> fetchBrukere() {

        return fetchOrCreateBruker()
                .flatMapMany(bruker -> Brukertype.AZURE == bruker.getBrukertype() ?
                        brukerRepository.findByOrderById() :
                        brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                                .map(TilgangDTO::getBrukere)
                                .flatMapMany(brukerRepository::findByBrukerIdInOrderByBrukernavn));
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

    private Mono<Testgruppe> fetchTestgruppe(Long gruppeId) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId)));
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
