package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.credentials.OrganisasjonServiceProperties;
import no.nav.testnav.libs.commands.organisasjonservice.v1.GetOrganisasjonCommand;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
@CacheConfig
public class OrganisasjonConsumer {
    private final OrganisasjonServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final ExecutorService executorService;

    public OrganisasjonConsumer(
            OrganisasjonServiceProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.executorService = Executors.newFixedThreadPool(serviceProperties.getThreads());
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    private CompletableFuture<OrganisasjonDTO> getFutureOrganisasjon(String orgnummer, AccessToken accessToken, String miljo) {
        return CompletableFuture.supplyAsync(
                () -> new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, miljo).call(),
                executorService
        );
    }

    @Cacheable("Mini-Norge-EREG")
    public List<OrganisasjonDTO> getOrganisasjoner(Set<String> orgnummerListe, String miljo) {
        AccessToken accessToken = tokenExchange.generateToken(serviceProperties).block();
        var futures = orgnummerListe.stream().map(value -> getFutureOrganisasjon(value, accessToken, miljo)).collect(Collectors.toList());
        List<OrganisasjonDTO> list = new ArrayList<>();

        for (CompletableFuture<OrganisasjonDTO> future : futures) {
            try {
                list.add(future.get());
            } catch (Exception e) {
                log.warn("Klarer ikke å hente ut alle oragnisasjoner", e);
            }
        }
        return list.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
