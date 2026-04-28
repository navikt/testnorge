package no.nav.testnav.libs.securitytokenservice;

import tools.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class StsOidcTokenService {
    private final WebClient webClient;

    private String token;
    private LocalDateTime expiry;

    public StsOidcTokenService(
            String url,
            String username,
            String password
    ) {
        webClient = WebClient
                .builder()
                .baseUrl(url)
                .defaultHeaders(header -> header.setBasicAuth(username, password))
                .build();
    }

    public Mono<String> getToken() {
        if (shouldRefresh()) {
            return updateToken();
        }
        if (hasExpired()) {
            throw new RuntimeException("Sikkerhet-token kunne ikke fornyes");
        }
        return Mono.just(token);
    }

    private boolean shouldRefresh() {
        return expiry == null || LocalDateTime.now().plusMinutes(1).isAfter(expiry);
    }

    private boolean hasExpired() {
        return expiry != null && LocalDateTime.now().isAfter(expiry);
    }

    private Mono<String> updateToken() {
        return webClient
                .get()
                .uri(builder -> builder
                        .queryParam("grant_type", "client_credentials")
                        .queryParam("scope", "openid")
                        .build()
                )
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(node -> {
                    expiry = LocalDateTime.now().plusSeconds(node.get("expires_in").asLong());
                    token = node.get("access_token").asText();
                }).map(value -> value.get("access_token").asText());
    }
}
