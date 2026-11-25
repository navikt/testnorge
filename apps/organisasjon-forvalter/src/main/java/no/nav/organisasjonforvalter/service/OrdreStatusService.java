package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.MiljoerServiceConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonServiceConsumer;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse.StatusEnv;
import no.nav.organisasjonforvalter.dto.responses.StatusDTO;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.organisasjonforvalter.jpa.repository.StatusRepository;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.COMPLETED;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.ERROR;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.FAILED;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.NOT_FOUND;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.RUNNING;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdreStatusService {

    private final StatusRepository statusRepository;
    private final MiljoerServiceConsumer miljoerServiceConsumer;
    private final OrganisasjonServiceConsumer organisasjonServiceConsumer;

    @Value("${ereg-dolly.status.tidsavbrudd:20m}")
    private Duration tidsavbrudd;

    public OrdreResponse getStatus(List<String> orgnumre) {

        var miljoer = miljoerServiceConsumer.getOrgMiljoer().block();

        var oppslag = orgnumre.stream()
                .distinct()
                .collect(Collectors.toMap(orgnr -> orgnr, orgnr -> slaaOpp(orgnr, miljoer)));

        var statusMap = statusRepository.findAllByOrganisasjonsnummerIn(orgnumre);

        var orgStatus = new ArrayList<BestillingStatus>();

        orgStatus.addAll(statusMap.stream()
                .filter(status -> !isFunnet(oppslag.get(status.getOrganisasjonsnummer()).get(status.getMiljoe())))
                .map(status -> getStatusIkkeFunnetIMiljoe(status, oppslag.get(status.getOrganisasjonsnummer()).get(status.getMiljoe())))
                .toList());

        orgStatus.addAll(oppslag.entrySet().stream()
                .flatMap(orgnrOppslag -> orgnrOppslag.getValue().entrySet().stream()
                        .filter(iMiljo -> isFunnet(iMiljo.getValue()))
                        .map(iMiljo -> BestillingStatus.builder()
                                .orgnummer(orgnrOppslag.getKey())
                                .status(StatusDTO.builder()
                                        .status(COMPLETED)
                                        .description("Fant organisasjonen i miljø")
                                        .build())
                                .miljoe(iMiljo.getKey())
                                .build()))
                .toList());

        orgStatus.addAll(oppslag.entrySet().stream()
                .filter(orgnrOppslag -> statusMap.stream()
                        .noneMatch(status -> orgnrOppslag.getKey().equals(status.getOrganisasjonsnummer())))
                .filter(orgnrOppslag -> orgnrOppslag.getValue().values().stream()
                        .noneMatch(OrdreStatusService::isFunnet))
                .map(orgnrOppslag -> BestillingStatus.builder()
                        .orgnummer(orgnrOppslag.getKey())
                        .status(StatusDTO.builder()
                                .status(NOT_FOUND)
                                .description("Ikke funnet")
                                .build())
                        .build())
                .toList());

        return OrdreResponse.builder()
                .orgStatus(orgStatus.stream()
                        .collect(Collectors.groupingBy(BestillingStatus::getOrgnummer))
                        .entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, org -> org.getValue().stream()
                                .map(value -> StatusEnv.builder()
                                        .status(nonNull(value.getStatus()) ? value.getStatus().getStatus() : ERROR)
                                        .details(nonNull(value.getStatus()) ? value.getStatus().getDescription() : "Se feilbeskrivelse")
                                        .environment(value.getMiljoe())
                                        .error(value.getFeilmelding())
                                        .build())
                                .toList())))
                .build();
    }

    private Map<String, OrganisasjonDTO> slaaOpp(String orgnr, Set<String> miljoer) {

        return organisasjonServiceConsumer.getStatus(Set.of(orgnr), miljoer)
                .flatMapIterable(Map::entrySet)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .block();
    }

    private static boolean isFunnet(OrganisasjonDTO organisasjon) {

        return nonNull(organisasjon) && isNotBlank(organisasjon.getOrgnummer());
    }

    private BestillingStatus getStatusIkkeFunnetIMiljoe(Status status, OrganisasjonDTO oppslagIMiljo) {

        var bestillingStatus = BestillingStatus.builder()
                .orgnummer(status.getOrganisasjonsnummer())
                .miljoe(status.getMiljoe());

        if (nonNull(status.getCreatedAt()) && status.getCreatedAt().plus(tidsavbrudd).isAfter(LocalDateTime.now())) {
            return bestillingStatus
                    .status(StatusDTO.builder()
                            .status(RUNNING)
                            .description("Sendt til ereg, venter på lasting")
                            .build())
                    .build();
        }

        if (nonNull(oppslagIMiljo) && isNotBlank(oppslagIMiljo.getError())) {
            log.warn("Klarte ikke å sjekke organisasjon {} i miljø {}: {}", status.getOrganisasjonsnummer(), status.getMiljoe(), oppslagIMiljo.getError());
            return bestillingStatus
                    .status(StatusDTO.builder()
                            .status(RUNNING)
                            .description("Klarte ikke å sjekke om organisasjonen finnes i ereg")
                            .build())
                    .build();
        }

        return bestillingStatus
                .status(StatusDTO.builder()
                        .status(FAILED)
                        .description("Organisasjonen ble ikke funnet i miljø innen %d minutter".formatted(tidsavbrudd.toMinutes()))
                        .build())
                .build();
    }
}
