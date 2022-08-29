package no.nav.testnav.proxies.pensjontestdatafacadeproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {
    private final static Logger log = LoggerFactory.getLogger(TestController.class);
    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("miljo1")
    public Object getMiljo1() {
        log.info("getMijo1");
        return restTemplate.getForEntity("https://pensjon-testdata-facade.dev.intern.nav.no/api/v1/miljo", String.class);
    }

    @GetMapping("miljo2")
    public Object getMiljo2() {
        log.info("getMijo2");
        return restTemplate.getForEntity("https://sam-q4.dev.intern.nav.no/isReady", String.class);
    }
}
