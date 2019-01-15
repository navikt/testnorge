package no.nav.registre.syntrest.controllers;
import no.nav.registre.syntrest.services.MeldekortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1")
public class MeldekortController {

    @Autowired
    private MeldekortService meldekortService;

    @GetMapping(value = "/generateMeldekort/{num_to_generate}/{meldegruppe}")
    public List<String> generateMeldekort(@PathVariable int num_to_generate, @PathVariable String meldegruppe) throws InterruptedException, ExecutionException {
        CompletableFuture<List<String>> result = meldekortService.generateMeldekortFromNAIS(num_to_generate, meldegruppe);
        return result.get();
    }
}
