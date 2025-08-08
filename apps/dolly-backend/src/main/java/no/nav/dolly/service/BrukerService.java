package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Bruker.Brukertype;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final GetUserInfo getUserInfo;
    private final TestgruppeRepository testgruppeRepository;

    public Mono<Bruker> fetchBruker(String brukerId) {

        return brukerRepository.findByBrukerId(brukerId)
                .switchIfEmpty(Mono.error(new NotFoundException("Bruker id: %s ikke funnet".formatted(brukerId))));
    }

    public Mono<Bruker> fetchOrCreateBruker(String brukerId) {

        if (isBlank(brukerId)) {
            return getAuthenticatedUserId.call()
                    .flatMap(this::fetchBruker)
                    .onErrorResume(NotFoundException.class, error -> createBruker());
        } else {
            return fetchBruker(brukerId)
                    .onErrorResume(NotFoundException.class, error -> createBruker());
        }
    }

    public Mono<Bruker> fetchOrCreateBruker() {

        return fetchOrCreateBruker(null);
    }

    @CacheEvict(value = {CACHE_BRUKER}, allEntries = true)
    public Mono<Bruker> createBruker() {

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

        return fetchOrCreateBruker()
                .flatMapMany(bruker -> Brukertype.AZURE == bruker.getBrukertype() ?
                        brukerRepository.findByOrderById() :
                        brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                                .map(TilgangDTO::getBrukere)
                                .flatMapMany(brukerRepository::findByBrukerIdInOrderByBrukernavn));
    }

    public Mono<Integer> sletteBrukerFavoritterByGroupId(Long groupId) {

        return brukerRepository.deleteBrukerFavoritterByGroupId(groupId);
    }

    private Mono<Testgruppe> fetchTestgruppe(Long gruppeId) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId)));
    }
}
