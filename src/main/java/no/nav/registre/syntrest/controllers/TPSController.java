package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.TPSService;
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
public class TPSController extends KubernetesUtils {

    @Value("${synth-tps-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private TPSService tpsService;

    @Autowired
    private RootController controller;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @GetMapping(value = "/tps/{endringskode}")
    public ResponseEntity generateTps(@PathVariable String endringskode, @RequestParam String numToGenerate) throws IOException, ApiException {
        if (!validation.validateEndringskode(endringskode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Not a valid endringskode. Needs to be one of " + validation.getEndringskoder().toString());
        }
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("endringskode", endringskode);
        requestParams.put("numToGenerate", numToGenerate);
        return controller.generate(appName, tpsService, requestParams, counter, lock, counterLock);
    }
}
