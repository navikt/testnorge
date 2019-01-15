package no.nav.registre.syntrest.providers;
import no.nav.registre.syntrest.services.MedlService;
import no.nav.registre.syntrest.services.MeldekortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1")
public class MedlController {

    @Autowired
    private MedlService medlService;

    @PostMapping(path = "generateMedl", consumes = "application/json", produces = "application/json")
    public List<String> generateMedl(@RequestBody int num_to_generate) throws InterruptedException, ExecutionException {
        CompletableFuture<List<String>> result = medlService.generateMedl(num_to_generate);
        return result.get();
    }
}
