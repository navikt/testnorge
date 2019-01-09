package no.nav.registre.hodejegeren.provider.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/internal")
public class InternalController {

    private String synthdataTpsIsReadyUrl;
    private String tpsfIsReadyUrl;
    private String identpoolIsReadyUrl;

    private RestTemplate restTemplate;

    public InternalController(RestTemplateBuilder restTemplateBuilder,
            @Value("${tps-syntetisereren.rest-api.url}") String synthdataTpsBaseUrl,
            @Value("${tps-forvalteren.rest-api.url}") String tpsfBaseUrl,
            @Value("${ident-pool.rest-api.url}") String identpoolBaseUrl) throws MalformedURLException {
        this.restTemplate = restTemplateBuilder.build();
        this.synthdataTpsIsReadyUrl = createIsReadyUrl(synthdataTpsBaseUrl);
        this.tpsfIsReadyUrl = createIsReadyUrl(tpsfBaseUrl);
        this.identpoolIsReadyUrl = createIsReadyUrl(identpoolBaseUrl);
    }

    @RequestMapping(value = "/isAlive", method = RequestMethod.GET)
    public String isAlive() {
        return "OK";
    }

    @RequestMapping(value = "/isReady", method = RequestMethod.GET)
    public ResponseEntity<?> isReady() {
        List<String> nonAvailableResources = new ArrayList<>();

        checkReadiness(synthdataTpsIsReadyUrl, nonAvailableResources);
        checkReadiness(tpsfIsReadyUrl, nonAvailableResources);
        checkReadiness(identpoolIsReadyUrl, nonAvailableResources);

        if (nonAvailableResources.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        log.error("Feil hos avhengigheter: {}", nonAvailableResources.toString());
        return new ResponseEntity<>(nonAvailableResources, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void checkReadiness(String isReadyUrl, List<String> nonAvailableResources) {
        try {
            restTemplate.getForEntity(isReadyUrl, String.class);
        } catch (HttpStatusCodeException e) {
            nonAvailableResources.add("HttpStatusCodeException når " + isReadyUrl + " ble kalt. Statuskode: " + e.getStatusCode());
        }
    }

    private String createIsReadyUrl(String baseUrl) throws MalformedURLException {
        URL url = new URL(baseUrl);
        return url.getProtocol() + "://" + url.getHost() + "/internal/isReady";
    }
}