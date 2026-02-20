package no.nav.registre.varslingerservice.adapter;

import lombok.RequiredArgsConstructor;
import no.nav.registre.varslingerservice.repository.BrukerRepository;
import no.nav.registre.varslingerservice.repository.MottattVarslingRepository;
import no.nav.registre.varslingerservice.repository.model.BrukerModel;
import no.nav.registre.varslingerservice.repository.model.MottattVarslingModel;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonVarslingAdapter {

    private final MottattVarslingRepository mottattVarslingRepository;
    private final BrukerRepository brukerRepository;
    private final VarslingerAdapter varslingerAdapter;
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final GetAuthenticatedToken getAuthenticatedToken;

    private Mono<BrukerModel> getBruker() {
        return getAuthenticatedToken.call()
                .flatMap(token -> {
                    if (token.isClientCredentials()) {
                        return Mono.error(new BadCredentialsException("Kan ikke hente ut bruker fra en ikke-personlig innlogging."));
                    }
                    return getAuthenticatedUserId.call();
                })
                .flatMap(id -> Mono.fromCallable(() -> brukerRepository
                        .findById(id)
                        .orElseGet(() -> brukerRepository.save(
                                BrukerModel
                                        .builder()
                                        .objectId(id)
                                        .build()
                        )))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    private Mono<Optional<MottattVarslingModel>> getMottattVarsling(String varslingId) {
        return getBruker()
                .flatMap(bruker -> Mono.fromCallable(() -> {
                    String objectId = bruker.getObjectId();
                    return mottattVarslingRepository
                            .findAllByBrukerObjectId(objectId)
                            .stream()
                            .filter(mottattVarslingModel -> mottattVarslingModel.getVarsling().getVarslingId().equals(varslingId))
                            .findFirst();
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<List<String>> getAll() {
        return getBruker()
                .flatMap(bruker -> Mono.fromCallable(() -> {
                    var mottattVarslinger = mottattVarslingRepository.findAllByBrukerObjectId(bruker.getObjectId());
                    return mottattVarslinger
                            .stream()
                            .map(value -> value.getVarsling().getVarslingId())
                            .toList();
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<String> save(String varslingId) {
        return getBruker()
                .flatMap(bruker -> Mono.fromCallable(() -> {
                    var varsling = varslingerAdapter.get(varslingId);

                    var existing = mottattVarslingRepository
                            .findAllByBrukerObjectId(bruker.getObjectId())
                            .stream()
                            .filter(m -> m.getVarsling().getVarslingId().equals(varsling.getVarslingId()))
                            .findFirst();

                    if (existing.isPresent()) {
                        return varsling.getVarslingId();
                    }

                    var saved = mottattVarslingRepository.save(
                            MottattVarslingModel
                                    .builder()
                                    .bruker(bruker)
                                    .varsling(varsling.toModel())
                                    .build()
                    );
                    return saved.getVarsling().getVarslingId();
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<Void> delete(String varslingId) {
        return getMottattVarsling(varslingId)
                .flatMap(opt -> {
                    if (opt.isPresent()) {
                        return Mono.fromCallable(() -> {
                            mottattVarslingRepository.delete(opt.get());
                            return null;
                        }).subscribeOn(Schedulers.boundedElastic()).then();
                    }
                    return Mono.empty();
                });
    }

    public Mono<Optional<String>> get(String varslingId) {
        return getMottattVarsling(varslingId)
                .map(opt -> opt.map(mottatt -> mottatt.getVarsling().getVarslingId()));
    }
}