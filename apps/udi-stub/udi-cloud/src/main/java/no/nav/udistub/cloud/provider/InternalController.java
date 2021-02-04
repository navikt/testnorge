package no.nav.udistub.cloud.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/isAlive")
    public void isAlive() {
        //It's alive
    }

    @GetMapping("/isReady")
    public void isReady() {
        //It's ready
    }
}