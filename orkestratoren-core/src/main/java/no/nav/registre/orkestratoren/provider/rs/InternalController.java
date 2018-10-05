package no.nav.registre.orkestratoren.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/isAlive", method = RequestMethod.GET)
    public String isAlive() {
        return "OK";
    }

    @RequestMapping(value = "/isReady", method = RequestMethod.GET)
    public String isReady() {
        return "OK";
    }
}