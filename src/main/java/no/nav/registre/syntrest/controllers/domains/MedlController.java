package no.nav.registre.syntrest.controllers.domains;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.controllers.RootController;
import no.nav.registre.syntrest.services.domains.MedlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class MedlController extends RootController {
    
    @Value("${synth-medl-app}")
    private String appName;

    @Autowired
    private MedlService medlService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @GetMapping(value = "/medl")
    public ResponseEntity generateMedl(@RequestParam int numToGenerate) throws IOException, ApiException {
        return generate(appName, medlService, numToGenerate, counter, lock, counterLock);
    }
}
