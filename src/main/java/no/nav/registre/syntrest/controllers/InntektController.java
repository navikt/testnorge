package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.InntektService;
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
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class InntektController extends KubernetesUtils {

    @Value("${synth-inntekt-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private RootController controller;

    @Autowired
    private InntektService inntektService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @PostMapping(value = "/inntekt")
    public ResponseEntity generateInntektsmeldinger(@RequestBody String[] fnrs) throws ApiException, IOException {
        if (!validation.validateFnrs(fnrs)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Fødselsnummer needs to be of type String and length 11.");
        }
        return controller.generate(appName, inntektService, fnrs, counter, lock, counterLock);
    }
}

