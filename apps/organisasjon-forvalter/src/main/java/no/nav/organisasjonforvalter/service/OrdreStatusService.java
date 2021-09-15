package no.nav.organisasjonforvalter.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingConsumer;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse.StatusEnv;
import no.nav.organisasjonforvalter.dto.responses.StatusDTO;
import no.nav.organisasjonforvalter.jpa.repository.StatusRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdreStatusService {

    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;
    private final StatusRepository statusRepository;

    public OrdreResponse getStatus(List<String> orgnumre) {

        var statusMap = statusRepository.findAllByOrganisasjonsnummer(orgnumre)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Ingen status funnet for gitte orgnumre"));

        if (statusMap.stream().anyMatch(status -> StringUtils.isBlank(status.getBestId()))) {
            statusMap.stream()
                    .map(organisasjonBestillingConsumer::getBestillingId)
                    .reduce(Flux.empty(), Flux::concat)
                    .collectList()
                    .block();
            statusRepository.saveAll(statusMap);
        }

        var orgStatus = statusMap.stream()
                .filter(status -> isNotBlank(status.getBestId()))
                .map(organisasjonBestillingConsumer::getBestillingStatus)
                .reduce(Flux.empty(), Flux::concat)
                .collectList()
                .block();

        if (nonNull(orgStatus) && statusMap.stream().anyMatch(status -> isBlank(status.getBestId()))) {
            orgStatus.addAll(statusMap.stream()
                    .filter(status -> isBlank(status.getBestId()))
                    .map(status -> BestillingStatus.builder()
                            .orgnummer(status.getOrganisasjonsnummer())
                            .miljoe(status.getMiljoe())
                            .status(StatusDTO.builder()
                                    .status(NOT_FOUND)
                                    .description("Bestilling venter på å starte")
                                    .build())
                            .build())
                    .collect(toList()));
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeployEntry {

        private String environment;
        private String uuid;
        private List<BestillingStatus.ItemDto> lastStatus;
    }
}
