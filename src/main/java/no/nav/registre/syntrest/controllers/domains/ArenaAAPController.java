package no.nav.registre.syntrest.controllers.domains;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.controllers.RootController;
import no.nav.registre.syntrest.services.domains.ArenaAAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class ArenaAAPController extends RootController {

    @Value("${synth-arena-aap-app}")
    private String appName;

    @Autowired
    private ArenaAAPService aapService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @GetMapping(value = "/arena/aap/11_5")
    public ResponseEntity generateArenaAAP11_5(@RequestParam String numToGenerate) throws IOException, ApiException {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("type", "115");
        requestParams.put("numToGenerate", numToGenerate);
        return generate(appName, aapService, requestParams, counter, lock, counterLock);
    }

    @GetMapping(value = "/arena/aap/nyRettighet")
    public ResponseEntity generateArenaAAPNyRettighet(@RequestParam String numToGenerate) throws IOException, ApiException {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("type", "nyRettighet");
        requestParams.put("numToGenerate", numToGenerate);
        return generate(appName, aapService, requestParams, counter, lock, counterLock);
    }
}
