package no.nav.registre.orkestratoren.provider.rs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/internal")
public class InternalController {

    private String hodejegerenIsReadyUrl;
    private String synthdataTpsIsReadyUrl;
    private String synthdataArenaIsReadyUrl;
    private String tpsfIsReadyUrl;
    private String identpoolIsReadyUrl;

    @Autowired
    private RestTemplate restTemplate;

    public InternalController(
            @Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenBaseUrl,
            @Value("${tps-syntetisereren.rest-api.url}") String synthdataTpsBaseUrl,
            @Value("${synthdata-arena-inntekt.rest-api.url}") String synthdataArenaBaseUrl,
            @Value("${tps-forvalteren.rest-api.url}") String tpsfBaseUrl,
            @Value("${ident-pool.rest-api.url}") String identpoolBaseUrl) throws MalformedURLException {
        this.hodejegerenIsReadyUrl = createIsReadyUrl(hodejegerenBaseUrl);
        this.synthdataTpsIsReadyUrl = createIsReadyUrl(synthdataTpsBaseUrl);
        this.synthdataArenaIsReadyUrl = createIsReadyUrl(synthdataArenaBaseUrl);
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

        checkReadiness(hodejegerenIsReadyUrl, nonAvailableResources);
        checkReadiness(synthdataTpsIsReadyUrl, nonAvailableResources);
        checkReadiness(synthdataArenaIsReadyUrl, nonAvailableResources);
        checkReadiness(tpsfIsReadyUrl, nonAvailableResources);
        checkReadiness(identpoolIsReadyUrl, nonAvailableResources);

        if (nonAvailableResources.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return new ResponseEntity<>(nonAvailableResources, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void checkReadiness(String isReadyUrl, List<String> nonAvailableResources) {
        try {
            if(!(restTemplate.getForEntity(isReadyUrl, String.class).getStatusCode().equals(HttpStatus.OK))) {
                nonAvailableResources.add(isReadyUrl + " is not ready");
            }
        } catch (HttpStatusCodeException e) {
            nonAvailableResources.add("HttpStatusCodeException når " + isReadyUrl + " ble kalt. Statuskode: " + e.getStatusCode());
        }
    }

    private String createIsReadyUrl(String baseUrl) throws MalformedURLException {
        URL url = new URL(baseUrl);
        return url.getProtocol() + "://" + url.getHost() + "/internal/isReady";
    }
}