package no.nav.testnav.proxies.pensjontestdatafacadeproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutionException;

@RestController
public class TestController {
    private final static Logger log = LoggerFactory.getLogger(TestController.class);
    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("miljo1")
    public Object getMiljo1() {
        log.info("getMijo1");
        return restTemplate.getForEntity("https://pensjon-testdata-facade.dev.intern.nav.no/api/v1/miljo", String.class);
    }

    @GetMapping("miljo3")
    public Object getMiljo3() throws ExecutionException, InterruptedException {
        log.info("getMijo3");
        var webClient = WebClient.builder()
                .baseUrl("https://pensjon-testdata-facade.dev.intern.nav.no")
                .build();
        var result = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/miljo")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "dolly")
                .retrieve()
                .toEntity(String.class)
                .toFuture()
                .get();
        return result;
    }

    @GetMapping("miljo2")
    public Object getMiljo2() {
        log.info("getMijo2");
        return restTemplate.getForEntity("https://sam-q4.dev.intern.nav.no/isReady", String.class);
    }
}
