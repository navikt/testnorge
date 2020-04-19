package no.nav.brregstub.rs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NaisController {

    @GetMapping("/isAlive")
    public HttpStatus isAlive() {
        return HttpStatus.OK;
    }

    @GetMapping("/isReady")
    public HttpStatus isReady() {
        return HttpStatus.OK;
    }
}
