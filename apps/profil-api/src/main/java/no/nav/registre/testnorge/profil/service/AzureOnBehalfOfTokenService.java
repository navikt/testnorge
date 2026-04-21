package no.nav.registre.testnorge.profil.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.securitycore.command.azuread.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureClientCredential;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AzureOnBehalfOfTokenService {

    private final WebClient webClient;
    private final AzureClientCredential clientCredential;
    private final GetAuthenticatedToken getAuthenticatedToken;

    AzureOnBehalfOfTokenService(
            WebClient webClient,
            AzureClientCredential clientCredential,
            GetAuthenticatedToken getAuthenticatedToken
    ) {
        log.info("Init custom AzureAd token exchange.");
        this.getAuthenticatedToken = getAuthenticatedToken;
        this.webClient = webClient
                .mutate()
                .baseUrl(clientCredential.getTokenEndpoint())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        this.clientCredential = clientCredential;
    }

    public Mono<AccessToken> exchange(String scope) {
        return getAuthenticatedToken.call()
                .flatMap(token -> new OnBehalfOfExchangeCommand(webClient, clientCredential, scope, token).call());
    }

}
