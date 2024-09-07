package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingConsumer;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse.StatusEnv;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import no.nav.organisasjonforvalter.dto.responses.StatusDTO;
import no.nav.organisasjonforvalter.jpa.repository.StatusRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.ADDING_TO_QUEUE;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.COMPLETED;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdreStatusService {

    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;
    private final StatusRepository statusRepository;
    private final ImportService importService;

    public OrdreResponse getStatus(List<String> orgnumre) {

        var statusMap = statusRepository.findAllByOrganisasjonsnummerIn(orgnumre);

        var orgnrSomMangler = orgnumre.stream()
                .filter(orgnr -> statusMap.stream()
                        .noneMatch(status -> orgnr.equals(status.getOrganisasjonsnummer())))
                .collect(Collectors.toSet());

        if (!orgnrSomMangler.isEmpty()) {

            return OrdreResponse.builder()
                    .orgStatus(orgnrSomMangler.stream()
                            .collect(Collectors.toMap(orgnr -> orgnr, orgnr -> {
                                var orgIMiljo = importService.getOrganisasjoner(orgnr, null);
                                return !orgIMiljo.isEmpty() ?
                                        orgIMiljo.keySet().stream()
                                                .map(env -> StatusEnv.builder()
                                                        .status(COMPLETED)
                                                        .environment(env)
                                                        .build())
                                                .toList() :
                                        List.of(StatusEnv.builder()
                                                .status(NOT_FOUND)
                                                .build());
                            })))
                    .build();
        }

        var oppdatertStatus = statusMap;
        if (statusMap.stream().anyMatch(status -> isBlank(status.getBestId()))) {

            oppdatertStatus = Flux.fromIterable(statusMap)
                    .flatMap(organisasjonBestillingConsumer::getBestillingId)
                    .collectList()
                    .block();

            statusRepository.saveAll(oppdatertStatus);
        }

        var orgStatus = Flux.fromIterable(oppdatertStatus)
                .filter(status -> isNotBlank(status.getBestId()))
                .flatMap(organisasjonBestillingConsumer::getBestillingStatus)
                .collectList()
                .block();

        var firstOrg = nonNull(orgStatus) ? orgStatus.stream().findFirst().orElse(null) : null;

        var orgIMiljoe = false;

        if (nonNull(firstOrg)) {
            Map<String, RsOrganisasjon> organisasjoner = importService.getOrganisasjoner(firstOrg.getOrgnummer(), Set.of(firstOrg.getMiljoe()));
            log.info("Leter etter organisasjoner i miljoe: {}", firstOrg.getMiljoe());
            if (!organisasjoner.isEmpty()) {
                log.info("Fant org: {} i miljoe: {}", firstOrg.getOrgnummer(), firstOrg.getMiljoe());
                orgIMiljoe = true;
            }
        }

        if (oppdatertStatus.stream().anyMatch(status -> isBlank(status.getBestId()))) {
            orgStatus.addAll(statusMap.stream()
                    .filter(status -> isBlank(status.getBestId()))
                    .map(status -> BestillingStatus.builder()
                            .orgnummer(status.getOrganisasjonsnummer())
                            .miljoe(status.getMiljoe())
                            .status(StatusDTO.builder()
                                    .status(ADDING_TO_QUEUE)
                                    .description("Bestilling venter på å starte")
                                    .build())
                            .build())
                    .toList());
        }

        if (orgIMiljoe) {
            return OrdreResponse.builder()
                    .orgStatus(orgStatus
                            .stream()
                            .collect(Collectors.groupingBy(BestillingStatus::getOrgnummer,
                                    mapping(status -> StatusEnv.builder()
                                                    .status(COMPLETED)
                                                    .details("Fant organisasjon i miljø")
                                                    .environment(status.getMiljoe())
                                                    .error(status.getFeilmelding())
                                                    .build(),
                                            toList()))))
                    .build();
        }

        return OrdreResponse.builder()
                .orgStatus((nonNull(orgStatus) ? orgStatus : new ArrayList<BestillingStatus>())
                        .stream()
                        .collect(Collectors.groupingBy(BestillingStatus::getOrgnummer,
                                mapping(status -> StatusEnv.builder()
                                                .status(nonNull(status.getStatus()) ?
                                                        status.getStatus().getStatus() : null)
                                                .details(nonNull(status.getStatus()) ?
                                                        status.getStatus().getDescription() : null)
                                                .environment(status.getMiljoe())
                                                .error(status.getFeilmelding())
                                                .build(),
                                        toList()))))
                .build();
    }
}
