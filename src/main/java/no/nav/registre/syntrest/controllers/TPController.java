package no.nav.registre.syntrest.controllers;

import no.nav.registre.syntrest.services.TPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1")
public class TPController {

    @Autowired
    private TPService tpService;

    @GetMapping(value = "/generateTp/{num_to_generate}")
    public List<Map<String, String>> generateTp(@PathVariable int num_to_generate) throws InterruptedException, ExecutionException {
        CompletableFuture<List<Map<String, String>>> result = tpService.generateTPFromNAIS(num_to_generate);
        return result.get();
    }
}
