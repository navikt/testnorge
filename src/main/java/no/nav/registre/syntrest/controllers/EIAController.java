package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.EIAService;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class EIAController extends KubernetesUtils {

    @Value("${synth-eia-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private RootController controller;

    @Autowired
    private EIAService eiaService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @PostMapping(value = "/eia")
    public ResponseEntity generateEia(@RequestBody List<Map<String, String>> request) throws IOException, ApiException {
        if (validation.validateEia(request) != true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Fødselsnummer needs to be of type String and length 11.");
        }
        return controller.generate(appName, eiaService, request, counter, lock, counterLock);
    }
}

