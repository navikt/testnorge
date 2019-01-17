package no.nav.registre.orkestratoren.provider.rs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/internal")
public class InternalController {

    private String testnorgeSkdIsReadyUrl;
    private String synthdataArenaIsReadyUrl;
    private String tpsfIsReadyUrl;

    private RestTemplate restTemplate;

    public InternalController(RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-skd.rest-api.url}") String testnorgeSkdBaseUrl,
            @Value("${synthdata-arena-inntekt.rest-api.url}") String synthdataArenaBaseUrl,
            @Value("${tps-forvalteren.rest-api.url}") String tpsfBaseUrl) throws MalformedURLException {
        this.restTemplate = restTemplateBuilder.build();
        this.testnorgeSkdIsReadyUrl = createIsReadyUrl(testnorgeSkdBaseUrl);
        this.synthdataArenaIsReadyUrl = createIsReadyUrl(synthdataArenaBaseUrl);
        this.tpsfIsReadyUrl = createIsReadyUrl(tpsfBaseUrl);
    }

    @RequestMapping(value = "/isAlive", method = RequestMethod.GET)
    public String isAlive() {
        return "OK";
    }

    @RequestMapping(value = "/isReady", method = RequestMethod.GET)
    public ResponseEntity<?> isReady() {
        List<String> nonAvailableResources = new ArrayList<>();

        checkReadiness(testnorgeSkdIsReadyUrl, nonAvailableResources);
        checkReadiness(synthdataArenaIsReadyUrl, nonAvailableResources);
        checkReadiness(tpsfIsReadyUrl, nonAvailableResources);

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