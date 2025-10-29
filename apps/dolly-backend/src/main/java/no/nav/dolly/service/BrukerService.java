package no.nav.dolly.service;

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
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Objects.isNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;
import static no.nav.dolly.util.CurrentAuthentication.getAuthUser;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrukerService {

    private final BrukerFavoritterRepository brukerFavoritterRepository;
    private final BrukerRepository brukerRepository;
    private final BrukerServiceConsumer brukerServiceConsumer;
    private final GetUserInfo getUserInfo;
    private final TestgruppeRepository testgruppeRepository;
    private final TeamBrukerRepository teamBrukerRepository;
    private final TeamRepository teamRepository;

    public Mono<Bruker> fetchBruker(String brukerId) {

        return brukerRepository.findByBrukerId(brukerId)
                .switchIfEmpty(Mono.error(new NotFoundException("Bruker med brukerId %s ikke funnet".formatted(brukerId))));
    }

    public Mono<Bruker> fetchOrCreateBruker(String brukerId) {

        if (isBlank(brukerId)) {
            return getUserInfo.call()
                    .map(UserInfoExtended::id)
                    .flatMap(this::fetchBrukerOrTeam)
                    .switchIfEmpty(createBruker());
        } else {
            return fetchBrukerOrTeam(brukerId)
                    .switchIfEmpty(createBruker());
        }
    }

    public Mono<Bruker> fetchOrCreateBruker() {

        return fetchOrCreateBruker(null);
    }

    public Mono<Bruker> fetchBrukerOrTeam() {

        return getUserInfo.call()
                .map(UserInfoExtended::id)
                .flatMap(this::fetchBrukerOrTeam);
    }

    private Mono<Bruker> fetchBrukerOrTeam(String brukerId) {

        return brukerRepository.findByBrukerId(brukerId)
                .flatMap(bruker -> isNull(bruker.getRepresentererTeam()) ?
                        Mono.just(bruker) :
                        teamRepository.findById(bruker.getRepresentererTeam())
                                .flatMap(team -> brukerRepository.findById(team.getBrukerId())))
                .switchIfEmpty(createBruker());
    }

    @CacheEvict(value = {CACHE_BRUKER}, allEntries = true)
    public Mono<Bruker> createBruker() {

        return getAuthUser(getUserInfo)
                .flatMap(brukerRepository::save);
    }

    public Mono<Bruker> fetchBrukerWithoutTeam(String brukerId) {

        if (isBlank(brukerId)) {
            return getUserInfo.call()
                    .map(UserInfoExtended::id)
                    .flatMap(brukerRepository::findByBrukerId)
                    .switchIfEmpty(createBruker());
        } else {
            return brukerRepository.findByBrukerId(brukerId)
                    .switchIfEmpty(createBruker());
        }
    }

    public Mono<Bruker> fetchBrukerWithoutTeam() {

        return fetchBrukerWithoutTeam(null);
    }

    public Flux<Team> fetchTeamsForCurrentBruker() {

        return fetchBrukerWithoutTeam()
                .flatMapMany(bruker ->
                        teamBrukerRepository.findAllByBrukerId(bruker.getId())
                                .flatMap(teamBruker -> teamRepository.findById(teamBruker.getTeamId())));
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

    public Mono<Bruker> fjernFavoritt(Long gruppeId) {

        return fetchTestgruppe(gruppeId)
                .flatMap(gruppe ->
                        fetchOrCreateBruker()
                                .flatMap(bruker -> brukerFavoritterRepository.deleteByBrukerIdAndGruppeId(
                                                bruker.getId(),
                                                gruppe.getId())
                                        .thenReturn(bruker)));
    }

    public Flux<Bruker> fetchBrukere() {

        return fetchBrukerWithoutTeam()
                .flatMapMany(bruker -> Brukertype.AZURE == bruker.getBrukertype() ?
                        brukerRepository.findByOrderById() :
                        brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                                .map(TilgangDTO::getBrukere)
                                .flatMapMany(brukerRepository::findByBrukerIdInOrderByBrukernavn));
    }

    private Mono<Testgruppe> fetchTestgruppe(Long gruppeId) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId)));
    }

    public Mono<Bruker> setRepresentererTeam(Long teamId) {

        return fetchBrukerWithoutTeam()
                .flatMap(bruker -> {

                    if (isNull(teamId)) {
                        bruker.setRepresentererTeam(null);
                        return brukerRepository.save(bruker);

                    } else {
                        return teamRepository.findById(teamId)
                                .switchIfEmpty(Mono.error(new NotFoundException("Fant ikke team med ID: " + teamId)))
                                .flatMap(team -> teamBrukerRepository.findByTeamIdAndBrukerId(teamId, bruker.getId())
                                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Bruker er ikke medlem av teamet med ID: " + teamId))))
                                .flatMap(team -> {
                                    bruker.setRepresentererTeam(teamId);
                                    return brukerRepository.save(bruker);
                                });
                    }
                });
    }

    public Mono<Bruker> findById(Long id) {

        return brukerRepository.findById(id);
    }
}
