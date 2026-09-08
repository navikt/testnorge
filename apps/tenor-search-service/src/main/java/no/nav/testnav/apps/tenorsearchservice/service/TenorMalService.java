package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.domain.OpprettTenorMalRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMal;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalBruker;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalBrukerResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalLagreResult;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalType;
import no.nav.testnav.apps.tenorsearchservice.domain.ValidertTenorMal;
import no.nav.testnav.apps.tenorsearchservice.exception.TenorMalConflictException;
import no.nav.testnav.apps.tenorsearchservice.exception.TenorMalNotFoundException;
import no.nav.testnav.apps.tenorsearchservice.repository.TenorMalRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TenorMalService {

    private static final String ALLE = "ALLE";

    private final CurrentTenorUserService currentUserService;
    private final TenorMalAccessService accessService;
    private final TenorMalValidationService validationService;
    private final TenorMalRepository malRepository;
    private final JsonMapper jsonMapper;
    private final TenorMalMetrics metrics;

    public Mono<TenorMalLagreResult> save(OpprettTenorMalRequest request) {
        return Mono.fromSupplier(() -> validationService.validate(request))
                .flatMap(validertMal -> currentUserService.getOrCreateCurrentUser()
                        .flatMap(bruker -> save(bruker, validertMal)))
                .doOnSuccess(_ -> metrics.success("save"))
                .doOnError(_ -> metrics.failure("save"));
    }

    public Flux<TenorMalResponse> getMaler(String brukerId, TenorMalType malType) {
        return currentUserService.getOrCreateCurrentUser()
                .flatMapMany(currentUser -> getTargetUsers(currentUser, brukerId)
                        .collectMap(TenorMalBruker::getId)
                        .flatMapMany(users -> findMaler(users.keySet().stream().toList(), malType)
                                .map(mal -> toResponse(mal, users.get(mal.getBrukerId())))))
                .doOnComplete(() -> metrics.success("list"))
                .doOnError(_ -> metrics.failure("list"));
    }

    public Flux<TenorMalBrukerResponse> getBrukere() {
        return currentUserService.getOrCreateCurrentUser()
                .flatMapMany(currentUser -> Flux.concat(
                        Mono.just(new TenorMalBrukerResponse(ALLE, ALLE)),
                        accessService.getAccessibleUsers(currentUser)
                                .map(this::toBrukerResponse)))
                .doOnComplete(() -> metrics.success("users"))
                .doOnError(_ -> metrics.failure("users"));
    }

    public Mono<TenorMalResponse> updateMalNavn(Long id, String malNavn) {
        return Mono.fromSupplier(() -> validationService.validateMalNavn(malNavn))
                .flatMap(validertMalNavn -> currentUserService.getOrCreateCurrentUser()
                        .flatMap(bruker -> updateMalNavn(id, bruker, validertMalNavn)))
                .doOnSuccess(_ -> metrics.success("rename"))
                .doOnError(_ -> metrics.failure("rename"));
    }

    public Mono<Void> delete(Long id) {
        return currentUserService.getOrCreateCurrentUser()
                .flatMap(bruker -> malRepository.deleteByIdAndBrukerId(id, bruker.getId()))
                .filter(deletedRows -> deletedRows > 0)
                .switchIfEmpty(Mono.error(new TenorMalNotFoundException("Malen ble ikke funnet.")))
                .then()
                .doOnSuccess(_ -> metrics.success("delete"))
                .doOnError(_ -> metrics.failure("delete"));
    }

    private Mono<TenorMalLagreResult> save(
            TenorMalBruker bruker,
            ValidertTenorMal validertMal
    ) {
        return malRepository.upsert(
                        validertMal.malNavn(),
                        validertMal.malNavnNormalisert(),
                        validertMal.malType().name(),
                        validertMal.soekKriterier(),
                        bruker.getId())
                .map(result -> new TenorMalLagreResult(
                        toResponse(result.toTenorMal(), bruker),
                        result.ny()));
    }

    private Flux<TenorMalBruker> getTargetUsers(
            TenorMalBruker currentUser,
            String requestedUserId
    ) {
        if (requestedUserId == null || currentUser.getBrukerId().equals(requestedUserId)) {
            return Flux.just(currentUser);
        }
        if (ALLE.equalsIgnoreCase(requestedUserId)) {
            return accessService.getAccessibleUsers(currentUser);
        }
        return accessService.getAccessibleUser(currentUser, requestedUserId).flux();
    }

    private Flux<TenorMal> findMaler(List<Long> brukerIds, TenorMalType malType) {
        if (brukerIds.isEmpty()) {
            return Flux.empty();
        }
        return malType == null ?
                malRepository.findByBrukerIdInOrderByMalNavnAscIdAsc(brukerIds) :
                malRepository.findByBrukerIdInAndMalTypeOrderByMalNavnAscIdAsc(brukerIds, malType);
    }

    private Mono<TenorMalResponse> updateMalNavn(
            Long id,
            TenorMalBruker bruker,
            String malNavn
    ) {
        var normalizedMalNavn = validationService.normalizeMalNavn(malNavn);
        return malRepository.existsByBrukerIdAndMalNavnNormalisertAndIdNot(
                        bruker.getId(),
                        normalizedMalNavn,
                        id)
                .flatMap(exists -> exists ?
                        Mono.error(new TenorMalConflictException("En mal med dette navnet finnes allerede.")) :
                        malRepository.updateMalNavn(id, bruker.getId(), malNavn, normalizedMalNavn))
                .filter(updatedRows -> updatedRows > 0)
                .switchIfEmpty(Mono.error(new TenorMalNotFoundException("Malen ble ikke funnet.")))
                .then(malRepository.findByIdAndBrukerId(id, bruker.getId()))
                .switchIfEmpty(Mono.error(new TenorMalNotFoundException("Malen ble ikke funnet.")))
                .map(mal -> toResponse(mal, bruker))
                .onErrorMap(
                        DataIntegrityViolationException.class,
                        _ -> new TenorMalConflictException("En mal med dette navnet finnes allerede."));
    }

    private TenorMalResponse toResponse(TenorMal mal, TenorMalBruker bruker) {
        try {
            return new TenorMalResponse(
                    mal.getId(),
                    mal.getMalNavn(),
                    mal.getMalType(),
                    jsonMapper.readTree(mal.getSoekKriterier()),
                    bruker.getBrukerId(),
                    bruker.getBrukernavn(),
                    mal.getOpprettet(),
                    mal.getSistOppdatert());
        } catch (JacksonException exception) {
            throw new IllegalStateException("Kunne ikke lese lagrede søkekriterier.", exception);
        }
    }

    private TenorMalBrukerResponse toBrukerResponse(TenorMalBruker bruker) {
        return new TenorMalBrukerResponse(bruker.getBrukerId(), bruker.getBrukernavn());
    }
}
