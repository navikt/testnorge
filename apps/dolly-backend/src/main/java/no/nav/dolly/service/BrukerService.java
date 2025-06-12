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
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Bruker fetchBruker(String brukerId) {

        return brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseThrow(() -> new NotFoundException("Bruker ikke funnet"));
    }

    public Mono<Bruker> fetchOrCreateBruker(String brukerId) {

        if (isBlank(brukerId)) {
            return getAuthenticatedUserId.call()
                    .map(this::fetchBruker)
                    .onErrorResume(NotFoundException.class, e -> createBruker());
        }
        try {
            return Mono.just(fetchBruker(brukerId));

        } catch (NotFoundException e) {
            return createBruker();
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

        return Mono.just(fetchTestgruppe(gruppeId))
                .map(gruppe -> getAuthenticatedUserId.call()
                        .map(this::fetchBruker)
                        .doOnNext(bruker -> gruppe.getFavorisertAv().add(bruker))
                        .doOnNext(bruker -> bruker.getFavoritter().add(gruppe))
                        .doOnNext(bruker -> brukerFavoritterRepository.save(BrukerFavoritter.builder()
                                .id(BrukerFavoritter.BrukerFavoritterId.builder()
                                        .brukerId(bruker.getId())
                                        .gruppeId(gruppe.getId())
                                        .build())
                                .build())))
                .flatMap(Mono::from);
    }

    public Mono<Bruker> fjernFavoritt(Long gruppeId) {

        return Mono.just(fetchTestgruppe(gruppeId))
                .map(gruppe -> getAuthenticatedUserId.call()
                        .map(this::fetchBruker)
                        .doOnNext(bruker -> bruker.getFavoritter().remove(gruppe))
                        .doOnNext(bruker -> brukerFavoritterRepository.delete(
                                BrukerFavoritter.builder()
                                        .id(BrukerFavoritter.BrukerFavoritterId.builder()
                                                .brukerId(bruker.getId())
                                                .gruppeId(gruppe.getId())
                                                .build())
                                        .build())))
                .flatMap(Mono::from);
    }

    public Mono<List<Bruker>> fetchBrukere() {

        return fetchOrCreateBruker()
                .map(bruker -> Brukertype.AZURE == bruker.getBrukertype() ?
                    Mono.just(brukerRepository.findAllByOrderById()) :
                        brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                    .map(TilgangDTO::getBrukere)
                    .map(brukerRepository::findAllByBrukerIdInOrderByBrukernavn))
                .flatMap(Mono::from);
    }

    public void sletteBrukerFavoritterByGroupId(Long groupId) {
        brukerRepository.deleteBrukerFavoritterByGroupId(groupId);
    }

    private Testgruppe fetchTestgruppe(Long gruppeId) {
        return testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId));
    }
}
