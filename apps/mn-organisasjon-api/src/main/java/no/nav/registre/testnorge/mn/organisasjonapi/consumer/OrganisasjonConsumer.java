package no.nav.registre.testnorge.mn.organisasjonapi.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.common.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.common.command.SaveOrganisasjonCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;
import no.nav.registre.testnorge.mn.organisasjonapi.credential.OrganisasjonApiClientCredential;

@Slf4j
@Component
public class OrganisasjonConsumer {
    private final ClientCredential clientCredential;
    private final ClientCredentialGenerateAccessTokenService clientCredentialGenerateAccessTokenService;
    private final WebClient webClient;
    private final ExecutorService executorService;

    public OrganisasjonConsumer(
            OrganisasjonApiClientCredential clientCredential,
            ClientCredentialGenerateAccessTokenService clientCredentialGenerateAccessTokenService,
            @Value("${consumers.organisasjonapi.url}") String baseUrl,
            @Value("${consumers.organisasjonapi.threads}") Integer threads
    ) {
        this.clientCredential = clientCredential;
        this.clientCredentialGenerateAccessTokenService = clientCredentialGenerateAccessTokenService;
        this.executorService = Executors.newFixedThreadPool(threads);
        this.webClient = WebClient
                .builder()
                .baseUrl(baseUrl)
                .build();
    }

    private CompletableFuture<OrganisasjonDTO> getFutureOrganisasjon(String orgnummer, AccessToken accessToken, String miljo) {
        return CompletableFuture.supplyAsync(
                () -> new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, miljo).call(),
                executorService
        );
    }

    public OrganisasjonDTO getOrganisjon(String orgnummer, String miljo) {
        AccessToken accessToken = clientCredentialGenerateAccessTokenService.generateToken(clientCredential);
        return new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, miljo).call();
    }

    public List<OrganisasjonDTO> getOrganisasjoner(Set<String> orgnummerListe, String miljo) {
        AccessToken accessToken = clientCredentialGenerateAccessTokenService.generateToken(clientCredential);
        var futures = orgnummerListe.stream().map(value -> getFutureOrganisasjon(value, accessToken, miljo)).collect(Collectors.toList());
        List<OrganisasjonDTO> list = new ArrayList<>();

        for (CompletableFuture<OrganisasjonDTO> future : futures) {
            try {
                list.add(future.get());
            } catch (Exception e) {
                throw new RuntimeException("Klarer ikke å hente ut alle oragnisasjoner", e);
            }
        }
        return list;
    }

    public void saveOrganisasjon(OrganisasjonDTO dto, String miljo) {
        AccessToken accessToken = clientCredentialGenerateAccessTokenService.generateToken(clientCredential);
        new SaveOrganisasjonCommand(webClient, dto, accessToken.getTokenValue(), miljo).run();
    }
}
