package no.nav.organisasjonforvalter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingConsumer;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse.StatusEnv;
import no.nav.organisasjonforvalter.dto.responses.StatusDTO;
import no.nav.organisasjonforvalter.jpa.repository.StatusRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.ADDING_TO_QUEUE;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.COMPLETED;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.ERROR;
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
    private final ObjectMapper objectMapper;

    public OrdreResponse getStatus(List<String> orgnumre) {

        var orgImiljo = orgnumre.stream()
                .map(orgnr -> importService.getOrganisasjoner(orgnr, null))
                .toList();

        var statusMap = statusRepository.findAllByOrganisasjonsnummerIn(orgnumre);

        var orgnrSomMangler = orgnumre.stream()
                .filter(orgnr -> statusMap.stream()
                        .noneMatch(status -> orgnr.equals(status.getOrganisasjonsnummer())))
                .filter(orgnr -> orgImiljo.stream()
                        .filter(iMiljoe -> !iMiljoe.isEmpty())
                        .flatMap(iMiljoe -> iMiljoe.values().stream())
                        .noneMatch(iMiljo -> orgnr.equals(iMiljo.getOrganisasjonsnummer())))
                .collect(Collectors.toSet());

        var oppdatertStatus = statusMap.stream()
                .filter(status -> orgnrSomMangler.stream()
                        .noneMatch(orgnr -> orgnr.equals(status.getOrganisasjonsnummer())))
                .toList();

        if (oppdatertStatus.stream().anyMatch(status -> isBlank(status.getBestId()))) {

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

        orgStatus.addAll(orgImiljo.stream()
                .filter(iMiljo -> !iMiljo.isEmpty())
                .map(iMiljo -> iMiljo.entrySet().stream()
                        .map(entry -> BestillingStatus.builder()
                                .orgnummer(entry.getValue().getOrganisasjonsnummer())
                                .status(StatusDTO.builder()
                                        .status(COMPLETED)
                                        .description("Fant organisasjonen i miljø")
                                        .build())
                                .miljoe(entry.getKey())
                                .build())
                        .toList())
                .flatMap(Collection::stream)
                .toList());

        orgStatus.addAll(orgnrSomMangler.stream()
                .map(orgnr -> BestillingStatus.builder()
                        .orgnummer(orgnr)
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
                                        .error(convertFeilmelding(value.getFeilmelding()))
                                        .build())
                                .toList())))
                .build();
    }

    private Object convertFeilmelding(String feilmelding) {

        try {
            return isNotBlank(feilmelding) ? objectMapper.readValue(feilmelding, JsonNode.class) : null;

        } catch (JsonProcessingException e) {
            return feilmelding;
        }
    }
}
