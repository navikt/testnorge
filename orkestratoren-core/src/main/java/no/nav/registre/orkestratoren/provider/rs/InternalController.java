package no.nav.registre.orkestratoren.provider.rs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String HODEJEGEREN_IS_READY_URL = "https://testnorge-hodejegeren.nais.preprod.local/internal/isReady";
    private static final String SYNTHDATA_TPS_IS_READY_URL = "https://synthdata-tps.nais.preprod.local/internal/isReady";
    private static final String SYNTHDATA_ARENA_IS_READY_URL = "https://synthdata-arena-inntekt.nais.preprod.local/internal/isReady";
    private static final String TPSF_IS_READY_URL = "https://tps-forvalteren.nais.preprod.local/internal/isReady";
    private static final String IDENTPOOL_IS_READY_URL = "https://ident-pool.nais.preprod.local/internal/isReady";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/isAlive", method = RequestMethod.GET)
    public String isAlive() {
        return "OK";
    }

    @RequestMapping(value = "/isReady", method = RequestMethod.GET)
    public ResponseEntity<?> isReady() {
        List<String> nonAvailableResources = new ArrayList<>();

        try {
            if (!(restTemplate.getForEntity(HODEJEGEREN_IS_READY_URL, String.class).getStatusCode().equals(HttpStatus.OK))) {
                nonAvailableResources.add("testnorge-hodejegeren is not ready");
            }
        } catch (HttpStatusCodeException e) {
            nonAvailableResources.add("HttpStatusCodeException when calling " + HODEJEGEREN_IS_READY_URL
                    + " - status code: " + e.getStatusCode());
        }

        try {
            if (!(restTemplate.getForEntity(SYNTHDATA_TPS_IS_READY_URL, String.class).getStatusCode().equals(HttpStatus.OK))) {
                nonAvailableResources.add("synthdata_tps is not ready");
            }
        } catch (HttpStatusCodeException e) {
            nonAvailableResources.add("HttpStatusCodeException when calling " + SYNTHDATA_TPS_IS_READY_URL
                    + " - status code: " + e.getStatusCode());
        }

        try {
            if (!(restTemplate.getForEntity(SYNTHDATA_ARENA_IS_READY_URL, String.class).getStatusCode().equals(HttpStatus.OK))) {
                nonAvailableResources.add("synthdata_arena is not ready");
            }
        } catch (HttpStatusCodeException e) {
            nonAvailableResources.add("HttpStatusCodeException when calling " + SYNTHDATA_ARENA_IS_READY_URL
                    + " - status code: " + e.getStatusCode());
        }

        try {
            if (!(restTemplate.getForEntity(TPSF_IS_READY_URL, String.class).getStatusCode().equals(HttpStatus.OK))) {
                nonAvailableResources.add("tps-forvalteren is not ready");
            }
        } catch (HttpStatusCodeException e) {
            nonAvailableResources.add("HttpStatusCodeException when calling " + TPSF_IS_READY_URL
                    + " - status code: " + e.getStatusCode());
        }

        try {
            if (!(restTemplate.getForEntity(IDENTPOOL_IS_READY_URL, String.class).getStatusCode().equals(HttpStatus.OK))) {
                nonAvailableResources.add("ident-pool is not ready");
            }
        } catch (HttpStatusCodeException e) {
            nonAvailableResources.add("HttpStatusCodeException when calling " + IDENTPOOL_IS_READY_URL
                    + " - status code: " + e.getStatusCode());
        }

        if (nonAvailableResources.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return new ResponseEntity<>(nonAvailableResources, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}