package no.nav.registre.orkestratoren.provider.rs;

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

    private String testnorgeSkdIsReadyUrl;
    private String testnorgeInntektIsReadyUrl;
    private String testnorgeAaregIsReadyUrl;
    private String eiasEmottakstubIsReadyUrl;
    private String testnorgeSigrunIsReadyUrl;
    private String testnorgeInstIsReadyUrl;

    private RestTemplate restTemplate;

    public InternalController(RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-skd.rest-api.url}") String testnorgeSkdBaseUrl,
            @Value("${inntekt.rest.api.url}") String testnorgeInntektBaseUrl,
            @Value("${testnorge-aareg.rest-api.url}") String testnorgeAaregBaseUrl,
            @Value("${eias-emottakstub.rest-api.url}") String eiasEmottakstubBaseUrl,
            @Value("${testnorge-sigrun.rest-api.url}") String testnorgeSigrunBaseUrl,
            @Value("${testnorge-inst.rest-api.url}") String testnorgeInstBaseUrl
    ) throws MalformedURLException {
        this.restTemplate = restTemplateBuilder.build();
        this.testnorgeSkdIsReadyUrl = createIsReadyUrl(testnorgeSkdBaseUrl);
        this.testnorgeInntektIsReadyUrl = createIsReadyUrl(testnorgeInntektBaseUrl);
        this.testnorgeAaregIsReadyUrl = createIsReadyUrl(testnorgeAaregBaseUrl);
        this.eiasEmottakstubIsReadyUrl = createIsReadyUrl(eiasEmottakstubBaseUrl);
        this.testnorgeSigrunIsReadyUrl = createIsReadyUrl(testnorgeSigrunBaseUrl);
        this.testnorgeInstIsReadyUrl = createIsReadyUrl(testnorgeInstBaseUrl);
    }

    @RequestMapping(value = "/isAlive", method = RequestMethod.GET)
    public String isAlive() {
        return "OK";
    }

    @RequestMapping(value = "/isReady", method = RequestMethod.GET)
    public ResponseEntity<?> isReady() {
        List<String> nonAvailableResources = new ArrayList<>();

        checkReadiness(testnorgeSkdIsReadyUrl, nonAvailableResources);
        checkReadiness(testnorgeInntektIsReadyUrl, nonAvailableResources);
        checkReadiness(testnorgeAaregIsReadyUrl, nonAvailableResources);
        checkReadiness(eiasEmottakstubIsReadyUrl, nonAvailableResources);
        checkReadiness(testnorgeSigrunIsReadyUrl, nonAvailableResources);
        checkReadiness(testnorgeInstIsReadyUrl, nonAvailableResources);

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