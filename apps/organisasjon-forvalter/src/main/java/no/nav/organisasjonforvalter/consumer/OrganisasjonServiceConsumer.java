package no.nav.organisasjonforvalter.consumer;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.organisasjonforvalter.config.credentials.OrganisasjonServiceProperties;
import no.nav.testnav.libs.commands.organisasjonservice.v1.GetOrganisasjonCommand;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Service
public class OrganisasjonServiceConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final OrganisasjonServiceProperties serviceProperties;
    private final ExecutorService executorService;

    public OrganisasjonServiceConsumer(
            OrganisasjonServiceProperties serviceProperties,
            TokenExchange tokenExchange) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
        this.executorService = Executors.newFixedThreadPool(serviceProperties.getThreads());
    }

    public Optional<OrganisasjonDTO> getStatus(String orgnummer, String miljoe) {

        try {
            return getStatus(Set.of(orgnummer), Set.of(miljoe)).get(miljoe).get(orgnummer);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Map<String, Map<String, Optional<OrganisasjonDTO>>> getStatus(Set<String> orgnummer, Set<String> miljoer) {

        long startTime = currentTimeMillis();

        var token = tokenExchange.generateToken(serviceProperties).block().getTokenValue();

        var completables = miljoer.stream()
                .map(miljoe -> OrgFutureDTO.builder()
                        .miljoe(miljoe)
                        .futureDto(orgnummer.stream()
                                .map(orgnr -> FutureDTO.builder()
                                        .orgnr(orgnr)
                                        .completableFuture(
                                                CompletableFuture.supplyAsync(() ->
                                                                new GetOrganisasjonCommand(webClient, token, orgnr, miljoe).call(),
                                                        executorService))
                                        .build())
                                .collect(Collectors.toMap(entry -> entry.getOrgnr(), entry -> entry.getCompletableFuture())))
                        .build())
                .collect(Collectors.toMap(entry -> entry.getMiljoe(), entry -> entry.getFutureDto()));

        var organisasjoner = completables.keySet().stream()
                .map(miljoe -> MiljoeOrgnrOrgDTO.builder()
                        .miljoe(miljoe)
                        .orgnrOrgDTO(
                                completables.get(miljoe).keySet().stream()
                                        .map(orgnr ->
                                                OrganisasjonServiceConsumer.OrgnrOrgDTO.builder()
                                                        .orgnr(orgnr)
                                                        .organisasjonDTO(resolveCompleteable(completables.get(miljoe).get(orgnr)))
                                                        .build())
                                        .collect(Collectors.toMap(entry -> entry.getOrgnr(), entry -> entry.getOrganisasjonDTO())))
                        .build())
                .collect(Collectors.toMap(entry -> entry.getMiljoe(), entry -> entry.getOrgnrOrgDTO()));

        log.info("Organisasjon-Service svarte med funnet etter {} ms", currentTimeMillis() - startTime);

        return organisasjoner;
    }

    private Optional<OrganisasjonDTO> resolveCompleteable(CompletableFuture<OrganisasjonDTO> future) {

        try {
            var result = future.get();
            return nonNull(result) ? Optional.of(result) : Optional.empty();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class OrgFutureDTO {

        private String miljoe;
        private Map<String, CompletableFuture<OrganisasjonDTO>> futureDto;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class FutureDTO {

        private String orgnr;
        private CompletableFuture<OrganisasjonDTO> completableFuture;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class MiljoeOrgnrOrgDTO {

        private String miljoe;
        private Map<String, Optional<OrganisasjonDTO>> orgnrOrgDTO;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class OrgnrOrgDTO {

        private String orgnr;
        private Optional<OrganisasjonDTO> organisasjonDTO;
    }
}
