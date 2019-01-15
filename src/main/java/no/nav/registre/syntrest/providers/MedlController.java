package no.nav.registre.syntrest.providers;
import no.nav.registre.syntrest.services.MedlService;
import no.nav.registre.syntrest.services.MeldekortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1")
public class MedlController {

    @Autowired
    private MedlService medlService;

    @GetMapping(value = "/generateMedl/{num_to_generate}")
    public List<Map<String, String>> generateMedl(@PathVariable int num_to_generate) throws InterruptedException, ExecutionException {
        System.out.println(num_to_generate);
        CompletableFuture<List<Map<String, String>>> result = medlService.generateMedl(num_to_generate);
        return result.get();
    }
}
