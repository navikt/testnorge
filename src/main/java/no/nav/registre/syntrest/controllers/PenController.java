package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.PenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class PenController extends KubernetesUtils {

    @Value("${synth-pen-app}")
    private String appName;

    @Autowired
    private PenService penService;

    @Autowired
    private RootController controller;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @GetMapping(value = "/pen")
    public ResponseEntity generatePen(@RequestParam int numToGenerate) throws IOException, ApiException {
        return controller.generate(appName, penService, numToGenerate, counter, lock, counterLock);
    }
}
