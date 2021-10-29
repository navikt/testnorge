package no.nav.freg.token.provider.utility.common;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class RestConsumer {

    private final RestTemplate rest;

    public RestConsumer(CookieStore cookieStore) {
        this.rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
                .disableRedirectHandling()
                .setDefaultCookieStore(cookieStore)
                .build()));
    }

    public <T> T post(
            String url,
            HttpEntity<?> request,
            Class<T> responseType
    ) {
        return rest.postForObject(url, request, responseType);
    }

    public <T> ResponseEntity<T> get(
            URI url,
            HttpEntity<?> request,
            Class<T> responseType
    ) {
        return rest.exchange(url, HttpMethod.GET, request, responseType);
    }
}
