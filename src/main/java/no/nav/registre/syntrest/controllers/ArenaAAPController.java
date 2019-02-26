package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.ArenaAAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class ArenaAAPController extends KubernetesUtils {

    @Value("${synth-arena-aap-app}")
    private String appName;

    @Autowired
    private RootController controller;

    @Autowired
    private ArenaAAPService aapService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @GetMapping(value = "/arena/aap")
    public ResponseEntity generateArenaAAP(@RequestParam int numToGenerate) throws IOException, ApiException {
        return controller.generate(appName, aapService, numToGenerate, counter, lock, counterLock);
    }
}
