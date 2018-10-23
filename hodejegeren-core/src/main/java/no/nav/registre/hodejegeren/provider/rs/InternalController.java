package no.nav.registre.hodejegeren.provider.rs;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {
    
    @RequestMapping(value = "/isAlive", method = RequestMethod.GET)
    public String isAlive() {
        return "OK";
    }
    
    @RequestMapping(value = "/isReady", method = RequestMethod.GET)
    public String isReady() {
        return "OK";
    }
}