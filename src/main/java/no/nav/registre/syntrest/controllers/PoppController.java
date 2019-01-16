package no.nav.registre.syntrest.controllers;

import no.nav.registre.syntrest.services.PoppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1")
public class PoppController {

    @Autowired
    private PoppService poppService;

    @PostMapping(value = "/generatePopp")
    public List<Map<String, Object>> generatePopp(@RequestBody String[] fnrs) throws InterruptedException, ExecutionException {
        CompletableFuture<List<Map<String, Object>>> result = poppService.generatePoppMeldingerFromNAIS(fnrs);
        return result.get();
    }
}
