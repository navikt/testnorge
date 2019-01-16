package no.nav.registre.syntrest.controllers;

import no.nav.registre.syntrest.Domain.Inntektsmelding;
import no.nav.registre.syntrest.services.ArenaInntektService;
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
public class ArenaInntektController {

    @Autowired
    private ArenaInntektService arenaInntektService;

    @PostMapping(value = "/generateArenaInntekt")
    public Map<String, List<Inntektsmelding>> generateInntektsmeldinger(@RequestBody String[] fnrs) throws InterruptedException, ExecutionException {
        CompletableFuture<Map<String, List<Inntektsmelding>>> result = arenaInntektService.generateInntektsmeldingerFromNAIS(fnrs);
        return result.get();
    }
}
