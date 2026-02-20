package no.nav.registre.orgnrservice.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orgnrservice.domain.Organisasjon;
import no.nav.registre.orgnrservice.repository.OrgnummerRepository;
import no.nav.registre.orgnrservice.repository.model.OrgnummerModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrgnummerAdapter {

    private final OrgnummerRepository organisasjonRepoistory;

    public Mono<Organisasjon> save(Organisasjon organisasjon) {
        return Mono.fromCallable(() -> {
            log.info("Lagrer orgnummer {}", organisasjon.getOrgnummer());
            OrgnummerModel orgFraDb = organisasjonRepoistory.findByOrgnummer(organisasjon.getOrgnummer());

            OrgnummerModel orgnummerModel = organisasjonRepoistory.save(
                    OrgnummerModel.builder()
                            .orgnummer(organisasjon.getOrgnummer())
                            .ledig(organisasjon.isLedig())
                            .id(orgFraDb == null ? null : orgFraDb.getId())
                            .build()
            );
            return new Organisasjon(orgnummerModel);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Organisasjon> saveAll(List<Organisasjon> organisasjoner) {
        return Flux.fromIterable(organisasjoner)
                .flatMap(this::save);
    }

    @Transactional
    public Mono<Void> deleteByOrgnummer(String orgnummer) {
        return hentByOrgnummer(orgnummer)
                .flatMap(org -> Mono.<Void>fromCallable(() -> {
                    log.info("Sletter orgnummer {}", orgnummer);
                    organisasjonRepoistory.deleteByOrgnummer(orgnummer);
                    return null;
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<List<Organisasjon>> hentAlleLedige() {
        return Mono.fromCallable(() -> {
            log.info("Henter alle ledige orgnr...");
            var orgModeller = organisasjonRepoistory.findAllByLedigIsTrue();
            return orgModeller.stream()
                    .map(Organisasjon::new)
                    .toList();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Organisasjon> hentByOrgnummer(String orgnummer) {
        return Mono.fromCallable(() -> {
            OrgnummerModel model = organisasjonRepoistory.findByOrgnummer(orgnummer);
            return model == null ? null : new Organisasjon(model);
        }).subscribeOn(Schedulers.boundedElastic())
                .flatMap(org -> org == null ? Mono.empty() : Mono.just(org));
    }
}
