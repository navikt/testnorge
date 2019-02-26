package no.nav.registre.syntrest.controllers.domains;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.controllers.RootController;
import no.nav.registre.syntrest.services.domains.AaregService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class AaregController extends RootController {

    @Value("${synth-aareg-app}")
    private String appName;

    @Autowired
    private AaregService aaregService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @PostMapping(value = "/aareg")
    public ResponseEntity generateAareg(@RequestBody String[] request) throws IOException, ApiException {
        return generate(appName, aaregService, request, counter, lock, counterLock);
    }
}