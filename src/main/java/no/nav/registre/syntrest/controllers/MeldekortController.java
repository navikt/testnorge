package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.MeldekortService;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class MeldekortController extends KubernetesUtils {

    @Value("${synth-arena-meldekort-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private MeldekortService meldekortService;

    @Autowired
    private RootController controller;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @GetMapping(value = "/arena/meldekort/{meldegruppe}")
    public ResponseEntity generateMeldekort(@PathVariable String meldegruppe, @RequestParam String numToGenerate) throws ApiException, IOException {
        if (!validation.validateMeldegruppe(meldegruppe)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Not a valid meldegruppe. Needs to be one of: ATTF, DAGP, INDI, ARBS, FY");
        }
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("meldegruppe", meldegruppe);
        requestParams.put("numToGenerate", numToGenerate);
        return controller.generate(appName, meldekortService, requestParams, counter, lock, counterLock);
    }
}
