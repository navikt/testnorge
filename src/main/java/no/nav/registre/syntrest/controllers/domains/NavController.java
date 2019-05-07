package no.nav.registre.syntrest.controllers.domains;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.controllers.RootController;
import no.nav.registre.syntrest.services.domains.NavService;
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
public class NavController extends RootController {

    @Value("${synth-nav-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private NavService navService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @GetMapping(value = "/nav/{endringskode}")
    public ResponseEntity generateNav(@PathVariable String endringskode, @RequestParam String numToGenerate) throws IOException, ApiException {
        if (!validation.validateNavEndringskode(endringskode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Not a valid endringskode. Needs to be one of " + validation.getNavEndringskoder().toString());
        }
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("endringskode", endringskode);
        requestParams.put("numToGenerate", numToGenerate);
        return generate(appName, navService, requestParams, counter, lock, counterLock);
    }
}
