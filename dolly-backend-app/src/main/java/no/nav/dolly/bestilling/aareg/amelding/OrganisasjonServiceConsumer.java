package no.nav.dolly.bestilling.aareg.amelding;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.OrganisasjonServiceProperties;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrganisasjonServiceConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final ExecutorService executorService;
    private final NaisServerProperties serviceProperties;

    public OrganisasjonServiceConsumer(TokenService tokenService, OrganisasjonServiceProperties serviceProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.executorService = Executors.newFixedThreadPool(serviceProperties.getThreads());
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public List<OrganisasjonDTO> getOrganisasjoner(Set<String> orgnummerListe, String miljo) {
        String accessToken = serviceProperties.getAccessToken(tokenService);
        var futures = orgnummerListe.stream().map(value -> getFutureOrganisasjon(value, accessToken, miljo)).collect(Collectors.toList());
        List<OrganisasjonDTO> list = new ArrayList<>();

        for (CompletableFuture<OrganisasjonDTO> future : futures) {
            try {
                list.add(future.get());
            } catch (Exception e) {
                throw new RuntimeException("Klarer ikke å hente ut alle organisasjoner", e);
            }
        }
        return list;
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private CompletableFuture<OrganisasjonDTO> getFutureOrganisasjon(String orgnummer, String accessToken, String miljo) {
        return CompletableFuture.supplyAsync(
                () -> new GetOrganisasjonCommand(webClient, accessToken, orgnummer, miljo).call(),
                executorService
        );
    }
}
