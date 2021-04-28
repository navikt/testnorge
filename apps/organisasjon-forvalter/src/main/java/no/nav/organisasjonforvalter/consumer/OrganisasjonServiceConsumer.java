package no.nav.organisasjonforvalter.consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.config.credentials.OrganisasjonServiceProperties;
import no.nav.registre.testnorge.libs.common.command.organisasjonservice.v1.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class OrganisasjonServiceConsumer {

    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final OrganisasjonServiceProperties serviceProperties;
    private final ExecutorService executorService;

    public OrganisasjonServiceConsumer(
            OrganisasjonServiceProperties serviceProperties,
            AccessTokenService accessTokenService) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.accessTokenService = accessTokenService;
        this.executorService = Executors.newFixedThreadPool(serviceProperties.getThreads());
    }

    public OrganisasjonDTO getStatus(String orgnummer, String miljoe) {

        try {
            return getStatus(Set.of(orgnummer), Set.of(miljoe)).get(miljoe).get(orgnummer);
        } catch (RuntimeException e) {
            return new OrganisasjonDTO();
        }
    }

    public Map<String, Map<String, OrganisasjonDTO>> getStatus(Set<String> orgnummer, Set<String> miljoer) {

        long startTime = currentTimeMillis();

        var token = accessTokenService.generateToken(serviceProperties).getTokenValue();

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
                                        .map(orgnr -> {
                                            try {
                                                return OrganisasjonServiceConsumer.OrgnrOrgDTO.builder()
                                                        .orgnr(orgnr)
                                                        .organisasjonDTO(completables.get(miljoe).get(orgnr).get())
                                                        .build();
                                            } catch (Exception e) {
                                                log.error(e.getMessage(), e);
                                            }
                                            return null;
                                        })
                                        .filter(Objects::nonNull)
                                        .filter(org -> nonNull(org.getOrganisasjonDTO()))
                                        .collect(Collectors.toMap(entry -> entry.getOrgnr(), entry -> entry.getOrganisasjonDTO())))
                        .build())
                .collect(Collectors.toMap(entry -> entry.getMiljoe(), entry -> entry.getOrgnrOrgDTO()));

        log.info("Organisasjon-Service svarte med funnet etter {} ms", currentTimeMillis() - startTime);

        return organisasjoner;
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
        private Map<String, OrganisasjonDTO> orgnrOrgDTO;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class OrgnrOrgDTO {

        private String orgnr;
        private OrganisasjonDTO organisasjonDTO;
    }
}
