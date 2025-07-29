package no.nav.dolly.bestilling.organisasjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse.EnvStatus;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import no.nav.dolly.service.OrganisasjonBestillingService;
import no.nav.dolly.service.OrganisasjonProgressService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonClient {

    public static final String FEIL_STATUS_ORGFORVALTER_DEPLOY = "FEIL= Mottok ikke status fra Org-Forvalter deploy";
    private static final String FEIL_UGYLDIGE_ORGNUMRE = "FEIL= Ugyldig deployment, liste med miljø eller orgnumre eksisterer ikke";
    private final OrganisasjonConsumer organisasjonConsumer;
    private final OrganisasjonProgressService organisasjonProgressService;
    private final OrganisasjonBestillingProgressRepository organisasjonBestillingProgressRepository;
    private final OrganisasjonBestillingService organisasjonBestillingService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    public Mono<Void> opprett(RsOrganisasjonBestilling request, OrganisasjonBestilling bestilling) {

        var bestillingRequest = BestillingRequest.builder()
                .organisasjoner(List.of(mapperFacade.map(request.getOrganisasjon(), BestillingRequest.SyntetiskOrganisasjon.class)))
                .build();

        var miljoer = request.getEnvironments();
        try {

            log.info("Bestiller orgnumre fra Organisasjonforvalter");
            return Flux.fromIterable(bestillingRequest.getOrganisasjoner())
                    .flatMap(organisasjon -> organisasjonConsumer.postOrganisasjon(bestillingRequest)
                            .flatMapMany(response -> Flux.fromIterable(response.getOrgnummer()))
                            .flatMap(orgnummer -> organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestilling.getId())
                                    .flatMap(organisasjonBestillingProgress -> {
                                        organisasjonBestillingProgress.setBestillingId(bestilling.getId());
                                        organisasjonBestillingProgress.setOrganisasjonsnummer(orgnummer);
                                        organisasjonBestillingProgress.setOrganisasjonsforvalterStatus(
                                                miljoer.stream().map(env -> env + ":Deployer")
                                                        .collect(Collectors.joining(",")));

                                        return organisasjonBestillingProgressRepository.save(organisasjonBestillingProgress)
                                                .thenReturn(orgnummer);
                                    }))
                            .collectList()
                            .map(HashSet::new)
                            .flatMap(orgnumre -> saveErrorToDb(orgnumre, bestilling.getId(), miljoer)
                                    .then(deployOrganisasjon(orgnumre, bestilling, miljoer))))
                    .collectList()
                    .then();

        } catch (RuntimeException e) {

            log.error("Feilet med å opprette organisasjon(er)", e);
            return organisasjonBestillingService.setBestillingFeil(bestilling.getId(), errorStatusDecoder.decodeThrowable(e))
                    .then(organisasjonProgressService.setBestillingFeil(bestilling.getId(), errorStatusDecoder.decodeThrowable(e)));
        }
    }

    public Mono<Void> gjenopprett(DeployRequest request, OrganisasjonBestilling bestilling) {

        var miljoer = request.getEnvironments();
        return organisasjonBestillingProgressRepository.save(OrganisasjonBestillingProgress.builder()
                        .bestillingId(bestilling.getId())
                        .organisasjonsnummer(request.getOrgnumre().iterator().next())
                        .organisasjonsforvalterStatus(miljoer.stream().map(env -> env + ":Deployer").collect(Collectors.joining(",")))
                        .build())
                .then(deployOrganisasjon(request.getOrgnumre(), bestilling, miljoer));
    }

    public void release(List<String> ignored) {
        throw new UnsupportedOperationException("Release ikke implementert");
    }

    private Mono<Void> saveErrorToDb(Set<String> orgnumre, Long bestillingId, Set<String> environments) {

        log.info("Deployer orgnumre fra Organisasjonforvalter");
        if (isNull(orgnumre) || orgnumre.isEmpty() || isNull(environments) || environments.isEmpty()) {
            return organisasjonBestillingService.setBestillingFeil(bestillingId, FEIL_UGYLDIGE_ORGNUMRE);
        }
        return Mono.empty();
    }

    private Mono<Void> deployOrganisasjon(Set<String> orgnumre, OrganisasjonBestilling bestilling, Set<String> environments) {

        return organisasjonConsumer.deployOrganisasjon(new DeployRequest(orgnumre, environments))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error(FEIL_STATUS_ORGFORVALTER_DEPLOY);
                    return organisasjonBestillingService.setBestillingFeil(bestilling.getId(), FEIL_STATUS_ORGFORVALTER_DEPLOY)
                            .then(Mono.empty());
                }))
                .flatMapMany(deployResponse -> Flux.fromIterable(deployResponse.getOrgStatus().keySet())
                        .flatMap(orgnummer -> Mono.just(deployResponse.getOrgStatus().get(orgnummer))
                                .flatMap(statuser -> {
                                    if (statuser.stream().anyMatch(EnvStatus::isError)) {
                                        return organisasjonBestillingService.setBestillingFeil(
                                                        bestilling.getId(),
                                                        deployResponse.getOrgStatus().get(orgnummer).stream()
                                                                .filter(EnvStatus::isError)
                                                                .map(EnvStatus::getDetails)
                                                                .findFirst().orElse(null))
                                                .thenReturn(statuser);
                                    } else {
                                        return Mono.just(statuser);
                                    }
                                })
                                .flatMap(statuser -> organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestilling.getId())
                                        .defaultIfEmpty(new OrganisasjonBestillingProgress())
                                        .flatMap(organisasjonBestillingProgress -> {

                                            organisasjonBestillingProgress.setBestillingId(bestilling.getId());
                                            organisasjonBestillingProgress.setOrganisasjonsnummer(orgnummer);
                                            organisasjonBestillingProgress.setOrganisasjonsforvalterStatus(mapStatusFraDeploy(orgnummer, deployResponse.getOrgStatus().get(orgnummer)));

                                            return organisasjonBestillingProgressRepository.save(organisasjonBestillingProgress);
                                        })
                                        .collectList())))
                .flatMap(Flux::fromIterable)
                .collectList()

                .then();
    }

    private String mapStatusFraDeploy(String orgnummer, List<EnvStatus> orgStatus) {

        if (isNull(orgStatus)) {
            return null;
        }
        var status = new StringBuilder();
        orgStatus.forEach(envStatus -> {

            log.info("Deploy har status: {} for org: {} i miljoe: {}",
                    envStatus.getStatus(),
                    orgnummer,
                    envStatus.getEnvironment());

            status.append(isNotBlank(status) ? ',' : "");
            status.append(envStatus.getEnvironment());
            status.append(':');
            status.append(envStatus.getStatus());
            if (nonNull(envStatus.getDetails())) {
                status.append("-");
                status.append(envStatus.getDetails());
            }
        });
        return status.toString();
    }
}
