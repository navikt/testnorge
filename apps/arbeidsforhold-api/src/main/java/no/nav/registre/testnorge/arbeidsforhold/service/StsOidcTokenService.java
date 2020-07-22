package no.nav.registre.testnorge.arbeidsforhold.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.arbeidsforhold.exception.SikkerhetsTokenExpiredException;


@Slf4j
@Service
public class StsOidcTokenService {


    private final RestTemplate restTemplate;
    private final String url;
    private final String username;
    private final String password;

    private String token;
    private LocalDateTime expiry;

    public StsOidcTokenService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${sts.token.provider.url}") String url,
            @Value("${sts.token.provider.username}") String username,
            @Value("${sts.token.provider.password}") String password
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
        this.password = password;
        this.username = username;
    }

    public String getToken() {
        updateTokenIfNeeded();
        return token;
    }

    private void updateTokenIfNeeded() {
        if (shouldRefresh()) {
            synchronized (this) {
                if (shouldRefresh()) {
                    try {
                        updateToken();
                    } catch (RuntimeException e) {
                        if (hasExpired()) {
                            throw new SikkerhetsTokenExpiredException("Sikkerhet-token kunne ikke fornyes", e);
                        }
                    }
                }
            }
        }
    }

    private boolean shouldRefresh() {
        return expiry == null || LocalDateTime.now().plusMinutes(1).isAfter(expiry);
    }

    private boolean hasExpired() {
        return expiry == null || LocalDateTime.now().isAfter(expiry);
    }

    private void updateToken() {
        ResponseEntity<?> responseEntity = restTemplate.exchange(RequestEntity
                        .get(URI.create((url)
                                .concat("?grant_type=client_credentials&scope=openid")))
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, "Basic " + Base64.getEncoder()
                                .encodeToString((username + ":" + password)
                                        .getBytes(UTF_8))
                        )
                        .build(),
                JsonNode.class
        );

        if (isNull(responseEntity.getBody())) {
            return;
        }
        expiry = LocalDateTime.now().plusSeconds(((JsonNode) responseEntity.getBody()).get("expires_in").asLong());
        token = ((JsonNode) responseEntity.getBody()).get("access_token").asText();
    }

}
