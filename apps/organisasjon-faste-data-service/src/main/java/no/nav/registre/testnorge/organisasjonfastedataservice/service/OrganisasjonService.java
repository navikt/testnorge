package no.nav.registre.testnorge.organisasjonfastedataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjonfastedataservice.repository.OrganisasjonRepository;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonService {
    private final OrganisasjonRepository repository;

    public Mono<Void> save(Organisasjon organisasjon, Gruppe gruppe) {
        return Mono.fromRunnable(() -> repository.save(organisasjon.toModel(gruppe)))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> delete(String orgnummer) {
        return Mono.fromRunnable(() -> repository.deleteById(orgnummer))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<List<Organisasjon>> getOrganisasjoner(Gruppe gruppe) {
        return Mono.fromCallable(() -> {
                    log.info("Henter alle organisasjoner med gruppe {}...", gruppe);
                    var list = repository.findAllByGruppe(gruppe);
                    var organisasjoner = list.stream().map(Organisasjon::new).collect(Collectors.toList());
                    log.info("Fant {} organisasjoner med gruppe {}.", organisasjoner.size(), gruppe);
                    return organisasjoner;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<Organisasjon>> getOrganisasjoner() {
        return Mono.fromCallable(() -> {
                    log.info("Henter alle organisasjoner...");
                    var list = repository.findAll();
                    var organisasjoner = StreamSupport
                            .stream(list.spliterator(), false)
                            .map(Organisasjon::new)
                            .toList();
                    log.info("Fant {} organisasjoner.", organisasjoner.size());
                    return organisasjoner;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Optional<Organisasjon>> getOrganisasjon(String orgnummer) {
        return Mono.fromCallable(() -> {
                    log.info("Henter organisasjon med orgnummer {}...", orgnummer);
                    var model = repository.findById(orgnummer);
                    if (model.isEmpty()) {
                        log.warn("Fant ikke organisasjon {}.", orgnummer);
                    }
                    return model.map(Organisasjon::new);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
