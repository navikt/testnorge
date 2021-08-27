package no.nav.organisasjonforvalter.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingConsumer;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse.EnvStatus;
import no.nav.organisasjonforvalter.jpa.repository.StatusRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdreStatusService {

    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;
    private final StatusRepository statusRepository;

    public OrdreResponse getStatus(List<String> orgnumre) {

        var statusMap = statusRepository.findAllByOrganisasjonsnummer(orgnumre)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Ingen status funnet for gitte orgnumre"));

        var orgStatus = statusMap.stream()
                .map(organisasjonBestillingConsumer::getBestillingStatus)
                .reduce(Flux.empty(), Flux::concat)
                .collectList()
                .block();

        return OrdreResponse.builder()
                .orgStatus((nonNull(orgStatus) ? orgStatus : new ArrayList<BestillingStatus>())
                        .stream()
                        .collect(Collectors.groupingBy(BestillingStatus::getOrgnummer,
                                mapping(status -> EnvStatus.builder()
                                                .status(status.getStatus().getDescription())
                                                .environment(status.getMiljoe())
                                                .details(status.getFeilmelding())
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
