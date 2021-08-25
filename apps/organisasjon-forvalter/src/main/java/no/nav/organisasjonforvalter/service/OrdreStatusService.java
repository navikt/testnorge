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

import static java.util.Objects.isNull;
import static no.nav.organisasjonforvalter.dto.responses.BestillingStatus.ItemDto.ItemStatus.INITIALIZING;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdreStatusService {

    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;
    private final StatusRepository statusRepository;

    public OrdreResponse getStatus(List<String> orgnumre) {

        var mapping = statusRepository.findAllByOrganisasjonsnummer(orgnumre)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Ingen status funnet for gitte orgnumre"));

        return OrdreResponse.builder()
                .orgStatus(
                        mapping.stream()
                                .map(entry -> organisasjonBestillingConsumer.getBestillingStatus(entry))
                                .reduce(Flux.empty(), Flux::concat)
                                .collectList()
                                .block()
                                .stream()
                                .collect(Collectors.toMap(BestillingStatus::getMiljoe,
                                        status -> EnvStatus.builder()
                                                .status(status.getItemDtos().stream()
                                                        .map(BestillingStatus.ItemDto::getStatus)
                                                        .map(BestillingStatus.ItemDto.ItemStatus::toString)
                                                        .findFirst().orElse(INITIALIZING.toString()))
                                                .details(status.getFeilmelding())
                                                .build())))
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeployEntry {

        private String environment;
        private String uuid;
        private List<BestillingStatus.ItemDto> lastStatus;

        public List<BestillingStatus.ItemDto> getLastStatus() {
            if (isNull(lastStatus)) {
                lastStatus = new ArrayList<>();
            }
            return lastStatus;
        }
    }
}
