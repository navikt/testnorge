package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.MiljoerServiceConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonMottakConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonServiceConsumer;
import no.nav.organisasjonforvalter.dto.requests.DeployRequest;
import no.nav.organisasjonforvalter.dto.responses.DeployResponse;
import no.nav.organisasjonforvalter.dto.responses.DeployResponse.EnvStatus;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.organisasjonforvalter.jpa.repository.StatusRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.organisasjonforvalter.dto.responses.DeployResponse.Status.ERROR;
import static no.nav.organisasjonforvalter.dto.responses.DeployResponse.Status.OK;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdreService {

    private final OrganisasjonRepository organisasjonRepository;
    private final OrganisasjonMottakConsumer organisasjonMottakConsumer;
    private final OrganisasjonServiceConsumer organisasjonServiceConsumer;
    private final StatusRepository statusRepository;
    private final MiljoerServiceConsumer miljoerServiceConsumer;

    public Mono<DeployResponse> deploy(DeployRequest request) {

        return miljoerServiceConsumer.getOrgMiljoer()
                .flatMap(tilgjengeligeMiljoer -> {
                    request.getEnvironments().forEach(miljoe -> {
                        if (tilgjengeligeMiljoer.stream().noneMatch(tilgjengelig -> tilgjengelig.equals(miljoe))) {
                            throw new ResponseStatusException(BAD_REQUEST, format("Miljoe %s eksisterer ikke", miljoe));
                        }
                    });

                    return Mono.fromCallable(() -> organisasjonRepository.findAllByOrganisasjonsnummerIn(request.getOrgnumre()))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .flatMap(organisasjoner -> {
                    if (organisasjoner.isEmpty()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, format("Ingen organisasjoner %s funnet!",
                                String.join(",", request.getOrgnumre()))));
                    }

                    return Flux.fromIterable(organisasjoner)
                            .flatMap(organisasjon -> Flux.fromIterable(request.getEnvironments())
                                    .flatMap(env -> {
                                        String uuid = UUID.randomUUID().toString();
                                        return deployOrganisasjon(uuid, organisasjon, env)
                                                .then(Mono.fromCallable(() -> statusRepository.save(Status.builder()
                                                        .uuid(uuid)
                                                        .organisasjonsnummer(organisasjon.getOrganisasjonsnummer())
                                                        .miljoe(env)
                                                        .build())).subscribeOn(Schedulers.boundedElastic()))
                                                .map(saved -> EnvStatus.builder()
                                                        .status(OK)
                                                        .environment(env)
                                                        .build())
                                                .onErrorResume(e -> {
                                                    log.error(e.getMessage(), e);
                                                    return Mono.just(EnvStatus.builder()
                                                            .status(ERROR)
                                                            .details(e.getMessage())
                                                            .environment(env)
                                                            .build());
                                                })
                                                .map(envStatus -> Map.entry(organisasjon.getOrganisasjonsnummer(), envStatus));
                                    }))
                            .collectList()
                            .map(entries -> DeployResponse.builder()
                                    .orgStatus(entries.stream()
                                            .collect(Collectors.groupingBy(Map.Entry::getKey,
                                                    Collectors.mapping(Map.Entry::getValue, Collectors.toList()))))
                                    .build());
                });
    }

    private Mono<Void> deployOrganisasjon(String uuid, Organisasjon organisasjon, String env) {

        return organisasjonServiceConsumer.getStatus(organisasjon.getOrganisasjonsnummer(), env)
                .collectList()
                .flatMap(orgStatus -> {
                    if (isNull(orgStatus) || orgStatus.stream()
                            .map(Map::entrySet)
                            .flatMap(Collection::stream)
                            .allMatch(org -> isBlank(org.getValue().getOrgnummer()))) {

                        return organisasjonMottakConsumer.opprettOrganisasjon(uuid, organisasjon, env);
                    } else {
                        return organisasjonMottakConsumer.endreOrganisasjon(uuid, organisasjon, env);
                    }
                });
    }
}
